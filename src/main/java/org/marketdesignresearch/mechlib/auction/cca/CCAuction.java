package org.marketdesignresearch.mechlib.auction.cca;

import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.marketdesignresearch.mechlib.auction.AuctionRound;
import org.marketdesignresearch.mechlib.domain.Bundle;
import org.marketdesignresearch.mechlib.domain.Domain;
import org.marketdesignresearch.mechlib.auction.Auction;
import org.marketdesignresearch.mechlib.domain.Good;
import org.marketdesignresearch.mechlib.domain.bid.Bids;
import org.marketdesignresearch.mechlib.domain.bidder.Bidder;
import org.marketdesignresearch.mechlib.domain.price.LinearPrices;
import org.marketdesignresearch.mechlib.domain.price.Price;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
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
        this(domain, mechanismType, true);
    }

    public CCAuction(Domain domain, MechanismFactory mechanismType, Prices currentPrices) {
        super(domain, mechanismType, false);
        this.currentPrices = currentPrices;
    }

    public CCAuction(Domain domain, MechanismFactory mechanismType, boolean proposeStartingPrices) {
        super(domain, mechanismType, proposeStartingPrices);
        if (!proposeStartingPrices) {
            this.currentPrices = new LinearPrices(getDomain().getGoods());
        }
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

    /**
     * In the base form of the CCA, we cannot assume much about any bidder.
     * So here, we pretend to know about the valuation of the bidder, taking 10 % of the average
     * of the bidders' values for the single goods as linear prices.
     * Note that in more sophisticated domains, you may want to override this and propose prices
     * based on what an auctioneer might know about the bidders / the market.
     */
    @Override
    protected void proposeStartingPrices() {
        Map<Good, Price> priceMap = new HashMap<>();
        getDomain().getGoods().forEach(good -> {
            BigDecimal price = BigDecimal.ZERO;
            for (Bidder bidder : getDomain().getBidders()) {
                price = price.add(bidder.getValue(Bundle.singleGoods(Sets.newHashSet(good))));
            }
            price = price.setScale(4, RoundingMode.HALF_UP)
                    .divide(BigDecimal.valueOf(getDomain().getBidders().size()), RoundingMode.HALF_UP) // Average
                    .divide(BigDecimal.TEN, RoundingMode.HALF_UP) // 10%
                    .setScale(2, RoundingMode.HALF_UP);
            priceMap.put(good, new Price(price));
        });
        currentPrices = new LinearPrices(priceMap);
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
        getDomain().getGoods().forEach(good -> demand.put(good, getLatestBids().getDemand(good)));
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
