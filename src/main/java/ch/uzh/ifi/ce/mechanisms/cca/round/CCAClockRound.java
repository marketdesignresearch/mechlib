package ch.uzh.ifi.ce.mechanisms.cca.round;

import ch.uzh.ifi.ce.domain.*;
import ch.uzh.ifi.ce.mechanisms.cca.Prices;
import ch.uzh.ifi.ce.demandquery.DemandQuery;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CCAClockRound extends CCARound {

    @Getter
    private Map<Good, Integer> demand = new HashMap<>();

    public CCAClockRound(int roundNumber, Prices prices, Set<? extends Bidder> bidders, DemandQuery demandQuery) {
        super(roundNumber, prices, bidders, demandQuery);
    }

    @Override
    public Bids collectBids() {

        Bids bids = new Bids();

        for (Bidder bidder : getBidders()) {
            Bundle bundle = getDemandQuery().getBundleBid(String.valueOf(getRoundNumber()), bidder, getPrices()).getBundle(); // We ignore the value here
            int totalQuantities = bundle.values().stream().mapToInt(i -> i).sum();
            if (totalQuantities > 0) {
                BigDecimal bidAmount = BigDecimal.ZERO;
                for (Map.Entry<Good, Integer> entry : bundle.entrySet()) {
                    Good good = entry.getKey();
                    demand.put(good, demand.getOrDefault(good, 0) + entry.getValue());
                    BigDecimal quantityTimesPrice = getPrices().get(entry.getKey()).getAmount().multiply(BigDecimal.valueOf(entry.getValue()));
                    bidAmount = bidAmount.add(quantityTimesPrice);
                }

                Bid bid = new Bid();
                BundleBid bundleBid = new BundleBid(bidAmount, bundle, "Bidder_" + bidder.getId() + "_Round_" + getRoundNumber() + "_" + bundle.toString());
                bid.addBundleBid(bundleBid);

                bids.setBid(bidder, bid);
            }

        }

        return bids;
    }
}
