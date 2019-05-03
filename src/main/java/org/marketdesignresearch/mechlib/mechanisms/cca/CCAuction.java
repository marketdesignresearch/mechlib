package org.marketdesignresearch.mechlib.mechanisms.cca;

import org.marketdesignresearch.mechlib.demandquery.DemandQuery;
import org.marketdesignresearch.mechlib.domain.*;
import org.marketdesignresearch.mechlib.mechanisms.AuctionMechanism;
import org.marketdesignresearch.mechlib.mechanisms.AuctionResult;
import org.marketdesignresearch.mechlib.mechanisms.cca.priceupdate.PriceUpdater;
import org.marketdesignresearch.mechlib.mechanisms.cca.priceupdate.SimpleRelativePriceUpdate;
import org.marketdesignresearch.mechlib.mechanisms.cca.round.CCAClockRound;
import org.marketdesignresearch.mechlib.mechanisms.cca.round.CCARound;
import org.marketdesignresearch.mechlib.mechanisms.cca.round.CCASupplementaryRound;
import org.marketdesignresearch.mechlib.mechanisms.cca.round.supplementaryround.ProfitMaximizingSupplementaryRound;
import org.marketdesignresearch.mechlib.mechanisms.cca.round.supplementaryround.SupplementaryRound;
import org.marketdesignresearch.mechlib.mechanisms.ccg.MechanismFactory;
import org.marketdesignresearch.mechlib.mechanisms.ccg.VariableAlgorithmCCGFactory;
import org.marketdesignresearch.mechlib.mechanisms.ccg.blockingallocation.XORBlockingCoalitionFinderFactory;
import org.marketdesignresearch.mechlib.mechanisms.ccg.constraintgeneration.ConstraintGenerationAlgorithm;
import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class CCAuction implements AuctionMechanism {

    private AuctionResult result;
    private List<CCARound> rounds = new ArrayList<>();
    private final Set<? extends Bidder> bidders;
    @Getter
    private final DemandQuery demandQuery;

    @Setter
    private PriceUpdater priceUpdater = new SimpleRelativePriceUpdate();
    private final List<SupplementaryRound> supplementaryRounds = new ArrayList<>();
    private Queue<SupplementaryRound> supplementaryRoundQueue = new LinkedList<>();

    @Setter
    private int maxRounds = 1000;
    private int roundCounter = 0;
    private Prices currentPrices;
    @Getter
    private boolean clockPhaseCompleted = false;

    public CCAuction(Set<Good> goods, Set<? extends Bidder> bidders, DemandQuery demandQuery) {
        this.currentPrices = new Prices(goods);
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
        supplementaryRoundQueue.add(supplementaryRound);
    }

    @Override
    public AuctionResult getAuctionResult() {
        if (result == null) {
            log.info("Running all clock rounds...");
            while (!clockPhaseCompleted) {
                nextClockRound();
            }
            if (supplementaryRounds.isEmpty()) addSupplementaryRound(new ProfitMaximizingSupplementaryRound(this));
            log.info("Running all supplementary rounds...");
            while (hasNextSupplementaryRound()) {
                nextSupplementaryRound();
            }
            log.info("Collected all bids. Running CCG Auction to determine allocation & payments.");
            AuctionInstance auctionInstance = new AuctionInstance(getLatestBids());
            MechanismFactory quadraticCCG = new VariableAlgorithmCCGFactory(new XORBlockingCoalitionFinderFactory(), ConstraintGenerationAlgorithm.STANDARD_CCG);
            AuctionMechanism mechanism = quadraticCCG.getMechanism(auctionInstance);
            result = mechanism.getAuctionResult();
        }
        return result;
    }

    public void nextClockRound() {
        if (clockPhaseCompleted) {
            log.warn("The clock phase is completed, there is no next clock round.");
            return;
        }
        CCAClockRound round = new CCAClockRound(roundCounter++, currentPrices, bidders, demandQuery);
        log.debug("Starting clock round {}...", roundCounter);
        round.getBids();
        rounds.add(round);
        Prices updatedPrices = priceUpdater.updatePrices(currentPrices, round.getDemand());
        if (currentPrices.equals(updatedPrices) || roundCounter >= maxRounds) {
            clockPhaseCompleted = true;
            return;
        }
        currentPrices = updatedPrices;
    }

    public void nextSupplementaryRound() {
        if (!clockPhaseCompleted) {
            log.warn("The supplementary round cannot be run before the clock phase has completed.");
            return;
        }
        if (supplementaryRoundQueue.isEmpty()) {
            log.warn("No supplementary round found to run");
            return;
        }
        SupplementaryRound supplementaryRound = supplementaryRoundQueue.poll();
        CCARound ccaRound = new CCASupplementaryRound(++roundCounter, currentPrices, bidders, demandQuery, supplementaryRound);
        log.debug("Starting supplementary round '{}'...", ccaRound);
        ccaRound.getBids();
        rounds.add(ccaRound);
    }

    public boolean hasNextSupplementaryRound() {
        return !supplementaryRoundQueue.isEmpty();
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
        Preconditions.checkArgument(round < rounds.size());
        Preconditions.checkArgument(rounds.get(round) instanceof CCAClockRound, "Currently, you can only reset the CCA to a round before the supplementary rounds.");
        clockPhaseCompleted = false;
        supplementaryRoundQueue = new LinkedList<>(supplementaryRounds);
        rounds = rounds.subList(0, round);
        result = null;
    }

}
