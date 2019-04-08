package ch.uzh.ifi.ce.mechanisms.cca;

import ch.uzh.ifi.ce.domain.*;
import ch.uzh.ifi.ce.mechanisms.AuctionMechanism;
import ch.uzh.ifi.ce.mechanisms.AuctionResult;
import ch.uzh.ifi.ce.demandquery.DemandQuery;
import ch.uzh.ifi.ce.mechanisms.cca.priceupdate.PriceUpdater;
import ch.uzh.ifi.ce.mechanisms.cca.priceupdate.SimpleRelativePriceUpdate;
import ch.uzh.ifi.ce.mechanisms.cca.round.CCAClockRound;
import ch.uzh.ifi.ce.mechanisms.cca.round.CCARound;
import ch.uzh.ifi.ce.mechanisms.cca.round.CCASupplementaryRound;
import ch.uzh.ifi.ce.mechanisms.cca.round.supplementaryround.ProfitMaximizingSupplementaryRound;
import ch.uzh.ifi.ce.mechanisms.cca.round.supplementaryround.SupplementaryRound;
import ch.uzh.ifi.ce.mechanisms.vcg.XORVCGAuction;
import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

public class CCAuction implements AuctionMechanism {

    private AuctionResult result;
    private List<CCARound> rounds = new ArrayList<>();
    private Set<Bidder> bidders;
    @Getter
    private DemandQuery demandQuery;

    @Setter
    private PriceUpdater priceUpdater = new SimpleRelativePriceUpdate();
    private List<SupplementaryRound> supplementaryRounds = new ArrayList<>();

    @Setter
    private int maxRounds = 1000;
    private int roundCounter = 0;
    private Prices currentPrices;
    private boolean clockPhaseCompleted = false;

    public CCAuction(Set<Good> goods, Set<Bidder> bidders, DemandQuery demandQuery) {
        currentPrices = new Prices(goods);
        this.bidders = bidders;
        this.demandQuery = demandQuery;
    }

    public CCAuction(Prices prices, Set<Bidder> bidders, DemandQuery demandQuery) {
        this.currentPrices = prices;
        this.bidders = bidders;
        this.demandQuery = demandQuery;
    }

    public void addSupplementaryRound(SupplementaryRound supplementaryRound) {
        supplementaryRounds.add(supplementaryRound);
    }

    @Override
    public AuctionResult getAuctionResult() {
        if (result == null) {
            while (!clockPhaseCompleted) {
                clockPhaseCompleted = nextClockRound();
            }
            if (supplementaryRounds.isEmpty()) supplementaryRounds.add(new ProfitMaximizingSupplementaryRound(this));
            for (SupplementaryRound supplementaryRound : supplementaryRounds) {
                runSupplementaryRound(supplementaryRound);
            }
            AuctionInstance auctionInstance = new AuctionInstance(getLatestBids());
            AuctionMechanism mechanism = new XORVCGAuction(auctionInstance);
            result = mechanism.getAuctionResult();
        }
        return result;
    }

    public boolean nextClockRound() {
        CCAClockRound round = new CCAClockRound(roundCounter++, currentPrices, bidders, demandQuery);
        round.getBids();
        rounds.add(round);
        Prices updatedPrices = priceUpdater.updatePrices(currentPrices, round.getDemand());
        if (currentPrices.equals(updatedPrices) || roundCounter >= maxRounds) {
            return true;
        }
        currentPrices = updatedPrices;
        return false;
    }

    public void runSupplementaryRound(SupplementaryRound supplementaryRound) {
        if (!clockPhaseCompleted) throw new IllegalStateException("The supplementary round cannot be run before the clock phase has completed.");
        CCARound ccaRound = new CCASupplementaryRound(++roundCounter, currentPrices, bidders, demandQuery, supplementaryRound);
        ccaRound.getBids();
        rounds.add(ccaRound);
    }

    public Bids getBidsAt(int round) {
        Preconditions.checkArgument(round >= 0 && round < rounds.size());
        return rounds.stream()
                .map(CCARound::getBids)
                .reduce(new Bids(), Bids::join);
    }

    public Bid getBidsAt(Bidder bidder, int round) {
        Preconditions.checkArgument(round >= 0 && round < rounds.size());
        return rounds.stream()
                .map(CCARound::getBids)
                .map(bids -> bids.getBid(bidder))
                .reduce(new Bid(), Bid::join);
    }

    public Bids getLatestBids() {
        return getBidsAt(rounds.size() - 1);
    }

    public Bid getLatestBids(Bidder bidder) {
        return getBidsAt(bidder, rounds.size() - 1);
    }

    public Prices getPricesAt(int round) {
        Preconditions.checkArgument(round >= 0 && round < rounds.size());
        return rounds.get(round).getPrices();
    }

    public Prices getLatestPrices() {
        return getPricesAt(rounds.size() - 1);
    }

    public void resetToRound(int round) {
        clockPhaseCompleted = false;
        rounds = rounds.subList(0, round);
    }

}
