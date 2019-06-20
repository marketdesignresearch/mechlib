package org.marketdesignresearch.mechlib.auction.cca;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.marketdesignresearch.mechlib.auction.AuctionRound;
import org.marketdesignresearch.mechlib.domain.Domain;
import org.marketdesignresearch.mechlib.auction.Auction;
import org.marketdesignresearch.mechlib.domain.Good;
import org.marketdesignresearch.mechlib.domain.bid.Bids;
import org.marketdesignresearch.mechlib.domain.price.LinearPrices;
import org.marketdesignresearch.mechlib.domain.price.Prices;
import org.marketdesignresearch.mechlib.mechanisms.Mechanism;
import org.marketdesignresearch.mechlib.mechanisms.MechanismResult;
import org.marketdesignresearch.mechlib.mechanisms.MechanismType;
import org.marketdesignresearch.mechlib.auction.cca.bidcollection.ClockPhaseBidCollector;
import org.marketdesignresearch.mechlib.auction.cca.bidcollection.SupplementaryBidCollector;
import org.marketdesignresearch.mechlib.auction.cca.bidcollection.supplementaryround.ProfitMaximizingSupplementaryRound;
import org.marketdesignresearch.mechlib.auction.cca.bidcollection.supplementaryround.SupplementaryRound;
import org.marketdesignresearch.mechlib.auction.cca.priceupdate.PriceUpdater;
import org.marketdesignresearch.mechlib.auction.cca.priceupdate.SimpleRelativePriceUpdate;
import org.marketdesignresearch.mechlib.mechanisms.ccg.MechanismFactory;

import java.util.*;

@Slf4j
public class CCAuction extends Auction {

    private MechanismResult result;
    private Prices currentPrices;

    @Setter
    private PriceUpdater priceUpdater = new SimpleRelativePriceUpdate();
    private final List<SupplementaryRound> supplementaryRounds = new ArrayList<>();
    private Queue<SupplementaryRound> supplementaryRoundQueue = new LinkedList<>();

    @Setter
    private int maxRounds = 1000;
    @Getter
    private boolean clockPhaseCompleted = false;

    public CCAuction(Domain domain) {
        this(domain, MechanismType.CCG);
    }

    public CCAuction(Domain domain, MechanismFactory mechanismType) {
        this(domain, mechanismType, new LinearPrices(domain.getGoods()));
    }

    public CCAuction(Domain domain, MechanismFactory mechanismType, Prices currentPrices) {
        super(domain, mechanismType);
        this.currentPrices = currentPrices;
    }

    @Override
    public int allowedNumberOfBids() {
        if (!clockPhaseCompleted) return 1;
        SupplementaryRound next = supplementaryRoundQueue.peek();
        return next == null ? 0 : next.getNumberOfSupplementaryBids();
    }

    @Override
    public Prices getCurrentPrices() {
        return currentPrices;
    }

    public void addSupplementaryRound(SupplementaryRound supplementaryRound) {
        supplementaryRounds.add(supplementaryRound);
        supplementaryRoundQueue.add(supplementaryRound);
    }

    @Override
    public MechanismResult getMechanismResult() {
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
            log.info("Collected all bids. Running {} Auction to determine allocation & payments.", getMechanismType());
            Mechanism mechanism = getMechanismType().getMechanism(getLatestAggregatedBids());
            result = mechanism.getMechanismResult();
        }
        return result;
    }

    @Override
    public void closeRound() {
        super.closeRound();
        updatePrices();
    }

    private void updatePrices() {
        Map<Good, Integer> demand = new HashMap<>();
        domain.getGoods().forEach(good -> demand.put(good, getLatestBids().getDemand(good)));
        Prices updatedPrices = priceUpdater.updatePrices(getCurrentPrices(), demand);
        if (getCurrentPrices().equals(updatedPrices) || getNumberOfRounds() >= maxRounds) {
            clockPhaseCompleted = true;
            return;
        }
        currentPrices = updatedPrices;
    }

    public void nextClockRound() {
        if (clockPhaseCompleted) {
            log.warn("The clock phase is completed, there is no next clock round.");
            return;
        }
        log.debug("Starting clock round {}...", getNumberOfRounds() + 1);
        ClockPhaseBidCollector collector = new ClockPhaseBidCollector(getNumberOfRounds() + 1, getCurrentPrices(), getDomain().getBidders());
        Bids bids = collector.collectBids();
        addRound(bids);
        updatePrices();
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
        SupplementaryRound supplementaryRound = supplementaryRoundQueue.peek();
        SupplementaryBidCollector collector = new SupplementaryBidCollector(getNumberOfRounds() + 1, getDomain().getBidders(), supplementaryRound);
        log.debug("Starting supplementary round '{}'...", collector);
        addRound(collector.collectBids());
        // TODO: To know at what supplementary round we are when adding the bids, we have this workaround
        //  of peek-poll. Later, it may make sense to have the current round noted somewhere (clock round 55? supplementary round 2? etc.)
        supplementaryRoundQueue.poll();
    }

    public boolean hasNextSupplementaryRound() {
        return !supplementaryRoundQueue.isEmpty();
    }

    @Override
    public void resetToRound(int index) {
        AuctionRound nextRound = getRound(index + 1);
        currentPrices = nextRound.getPrices();
        clockPhaseCompleted = false;
        supplementaryRoundQueue = new LinkedList<>(supplementaryRounds);
        result = null;
        super.resetToRound(index);
    }

}
