package org.marketdesignresearch.mechlib.mechanisms.cca;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.marketdesignresearch.mechlib.demandquery.DemandQuery;
import org.marketdesignresearch.mechlib.domain.Auction;
import org.marketdesignresearch.mechlib.domain.Domain;
import org.marketdesignresearch.mechlib.mechanisms.AuctionMechanism;
import org.marketdesignresearch.mechlib.mechanisms.AuctionResult;
import org.marketdesignresearch.mechlib.mechanisms.Mechanism;
import org.marketdesignresearch.mechlib.mechanisms.cca.bidcollection.ClockPhaseBidCollector;
import org.marketdesignresearch.mechlib.mechanisms.cca.bidcollection.SupplementaryBidCollector;
import org.marketdesignresearch.mechlib.mechanisms.cca.bidcollection.supplementaryround.ProfitMaximizingSupplementaryRound;
import org.marketdesignresearch.mechlib.mechanisms.cca.bidcollection.supplementaryround.SupplementaryRound;
import org.marketdesignresearch.mechlib.mechanisms.cca.priceupdate.PriceUpdater;
import org.marketdesignresearch.mechlib.mechanisms.cca.priceupdate.SimpleRelativePriceUpdate;
import org.marketdesignresearch.mechlib.mechanisms.ccg.MechanismFactory;
import org.marketdesignresearch.mechlib.mechanisms.ccg.VariableAlgorithmCCGFactory;
import org.marketdesignresearch.mechlib.mechanisms.ccg.blockingallocation.XORBlockingCoalitionFinderFactory;
import org.marketdesignresearch.mechlib.mechanisms.ccg.constraintgeneration.ConstraintGenerationAlgorithm;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Slf4j
public class CCAuction extends Auction {

    private AuctionResult result;
    private List<Prices> prices = new ArrayList<>();
    @Getter
    private final DemandQuery demandQuery;

    @Setter
    private PriceUpdater priceUpdater = new SimpleRelativePriceUpdate();
    private final List<SupplementaryRound> supplementaryRounds = new ArrayList<>();
    private Queue<SupplementaryRound> supplementaryRoundQueue = new LinkedList<>();

    @Setter
    private int maxRounds = 1000;
    @Getter
    private boolean clockPhaseCompleted = false;

    public CCAuction(Domain domain, DemandQuery demandQuery) {
        this(domain, Mechanism.CCG, demandQuery);
    }

    public CCAuction(Domain domain, Mechanism mechanism, DemandQuery demandQuery) {
        this(domain, new Prices(domain.getGoods()), mechanism, demandQuery);
    }

    public CCAuction(Domain domain, Prices prices, DemandQuery demandQuery) {
        this(domain, prices, Mechanism.CCG, demandQuery);
    }

    public CCAuction(Domain domain, Prices prices, Mechanism mechanism, DemandQuery demandQuery) {
        super(domain, mechanism);
        this.prices.add(prices);
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
            log.info("Collected all bids. Running {} Auction to determine allocation & payments.", getMechanism());
            AuctionMechanism mechanism = getMechanism().create(getLatestBids());
            result = mechanism.getAuctionResult();
        }
        return result;
    }

    public void nextClockRound() {
        if (clockPhaseCompleted) {
            log.warn("The clock phase is completed, there is no next clock round.");
            return;
        }
        int roundCounter = getRounds() + 1;
        log.debug("Starting clock round {}...", roundCounter);
        ClockPhaseBidCollector collector = new ClockPhaseBidCollector(roundCounter, getLatestPrices(), getDomain().getBidders(), demandQuery);
        addRound(collector.collectBids());
        Prices updatedPrices = priceUpdater.updatePrices(getLatestPrices(), collector.getDemand());
        if (getLatestPrices().equals(updatedPrices) || roundCounter >= maxRounds) {
            clockPhaseCompleted = true;
            return;
        }
        prices.add(updatedPrices);
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
        SupplementaryBidCollector collector = new SupplementaryBidCollector(getRounds() + 1, getLatestPrices(), getDomain().getBidders(), supplementaryRound);
        log.debug("Starting supplementary round '{}'...", collector);
        addRound(collector.collectBids());
    }

    public boolean hasNextSupplementaryRound() {
        return !supplementaryRoundQueue.isEmpty();
    }

    public Prices getPricesAt(int round) {
        Preconditions.checkArgument(round >= 0 && round < prices.size());
        return prices.get(round);
    }

    public Prices getLatestPrices() {
        return getPricesAt(prices.size() - 1);
    }

    public void resetToRound(int round) {
        Preconditions.checkArgument(round < prices.size());
        clockPhaseCompleted = false;
        supplementaryRoundQueue = new LinkedList<>(supplementaryRounds);
        result = null;
        super.resetToRound(round);
    }

}
