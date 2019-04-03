package ch.uzh.ifi.ce.mechanisms.cca;

import ch.uzh.ifi.ce.domain.*;
import ch.uzh.ifi.ce.mechanisms.AuctionMechanism;
import ch.uzh.ifi.ce.mechanisms.AuctionResult;
import ch.uzh.ifi.ce.mechanisms.cca.demandquery.DemandQuery;
import ch.uzh.ifi.ce.mechanisms.cca.priceupdate.PriceUpdater;
import ch.uzh.ifi.ce.mechanisms.cca.priceupdate.SimpleRelativePriceUpdate;
import ch.uzh.ifi.ce.mechanisms.cca.supplementaryround.ProfitMaximizingSupplementaryRound;
import ch.uzh.ifi.ce.mechanisms.cca.supplementaryround.SupplementaryRound;
import ch.uzh.ifi.ce.mechanisms.vcg.XORVCGAuction;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.*;

// FIXME: Have a class like CCARound.java and ClockPhaseRound.java/SupplementaryRound.java and manage them here
public class CCAuction implements AuctionMechanism {

    private AuctionResult result;
    @Setter
    private PriceUpdater priceUpdater = new SimpleRelativePriceUpdate();
    private List<SupplementaryRound> supplementaryRounds = new ArrayList<>();
    private Bids bids; // FIXME: Have bids per round

    @Getter
    private Map<Good, Price> prices; // FIXME: Have prices per round
    private Map<Good, Integer> demand; // FIXME: Have demand per round
    private Set<Bidder> bidders;
    @Getter
    private DemandQuery demandQuery;
    private int totalRounds = 1;
    @Setter
    private int maxRounds = 1000;

    public CCAuction(Set<Good> goods, Set<Bidder> bidders, DemandQuery demandQuery) {
        prices = new HashMap<>();
        goods.forEach(g -> prices.put(g, Price.ZERO)); // TODO: Have starting prices
        this.bidders = bidders;
        this.demandQuery = demandQuery;
    }

    @Override
    public AuctionResult getAuctionResult() {
        if (result == null) {
            runClockPhase();
            runSupplementaryRound();
            AuctionInstance auctionInstance = new AuctionInstance(bids);
            AuctionMechanism mechanism = new XORVCGAuction(auctionInstance);
            result = mechanism.getAuctionResult();
        }
        return result;
    }

    private void runClockPhase() {
        // reset current bids
        bids = new Bids();
        boolean done = false;
        while (!done) {
            demand = new HashMap<>();
            for (Bidder bidder : bidders) {
                Bundle bundle = demandQuery.getBundleBid(bidder, prices).getBundle(); // We ignore the value here
                int totalQuantities = bundle.values().stream().mapToInt(i -> i).sum();
                if (totalQuantities > 0) {
                    BigDecimal bidAmount = BigDecimal.ZERO;
                    for (Map.Entry<Good, Integer> entry : bundle.entrySet()) {
                        Good good = entry.getKey();
                        demand.put(good, demand.getOrDefault(good, 0) + entry.getValue());
                        BigDecimal quantityTimesPrice = prices.get(entry.getKey()).getAmount().multiply(BigDecimal.valueOf(entry.getValue()));
                        bidAmount = bidAmount.add(quantityTimesPrice);
                    }

                    Bid bid = bids.getBid(bidder);
                    if (bid == null) bid = new Bid();
                    // Check if such a bundle has already been bid on
                    BundleBid bundleBid = bid.getBundleBid(bundle);
                    if (bundleBid == null) {
                        bundleBid = new BundleBid(bidAmount, bundle, "Bidder_" + bidder.getId() + "_" + bundle.toString());
                    } else if (bundleBid.getAmount().compareTo(bidAmount) < 0) {
                        bundleBid = bundleBid.withAmount(bidAmount);
                    }

                    bid.addBundleBid(bundleBid);

                    bids.setBid(bidder, bid);
                }
            }

            Map<Good, Price> updatedPrices = priceUpdater.updatePrices(prices, demand);
            if (prices.equals(updatedPrices) || totalRounds >= maxRounds) {
                done = true;
            } else {
                prices = updatedPrices;
                totalRounds++;
            }
        }
    }


    private void runSupplementaryRound() {
        // FIXME: These supplementary rounds should not have to be supplied with the CCAuction
        if (supplementaryRounds.isEmpty()) supplementaryRounds.add(new ProfitMaximizingSupplementaryRound(this));

        for (Bidder bidder : bidders) {
            Bid bid = bids.getBid(bidder);
            if (bid == null) bid = new Bid();
            for (SupplementaryRound supplementaryRound : supplementaryRounds) {
                Set<BundleBid> newBundleBids = supplementaryRound.getSupplementaryBids(bidder).getBundleBids();
                newBundleBids.forEach(bid::addBundleBid);
            }

            bids.setBid(bidder, bid);
        }
    }

    public Map<Good, Price> getLastDemandedPrices() {
        return priceUpdater.getLastPrices();
    }
}
