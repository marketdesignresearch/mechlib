package org.marketdesignresearch.mechlib.auction.cca;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.marketdesignresearch.mechlib.auction.Auction;
import org.marketdesignresearch.mechlib.auction.AuctionRoundBuilder;
import org.marketdesignresearch.mechlib.auction.cca.bidcollection.ClockPhaseBidCollector;
import org.marketdesignresearch.mechlib.auction.cca.bidcollection.SupplementaryBidCollector;
import org.marketdesignresearch.mechlib.auction.cca.bidcollection.supplementaryround.SupplementaryRound;
import org.marketdesignresearch.mechlib.auction.cca.priceupdate.PriceUpdater;
import org.marketdesignresearch.mechlib.auction.cca.priceupdate.SimpleRelativePriceUpdate;
import org.marketdesignresearch.mechlib.core.BundleBid;
import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.bid.Bid;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.price.LinearPrices;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.mechanisms.MechanismResult;
import org.marketdesignresearch.mechlib.mechanisms.MechanismType;
import org.marketdesignresearch.mechlib.mechanisms.ccg.MechanismFactory;

import java.util.*;
import java.util.stream.Collectors;

import static org.marketdesignresearch.mechlib.auction.cca.CCARound.Type.CLOCK;
import static org.marketdesignresearch.mechlib.auction.cca.CCARound.Type.SUPPLEMENTARY;

@Slf4j
public class CCAuction extends Auction {

    @Getter
    private Prices currentPrices;

    @Setter
    private PriceUpdater priceUpdater = new SimpleRelativePriceUpdate();
    private final List<SupplementaryRound> supplementaryRounds = new ArrayList<>();
    private Queue<SupplementaryRound> supplementaryRoundQueue = new LinkedList<>();

    @Getter
    private boolean clockPhaseCompleted = false;

    public CCAuction(Domain domain) {
        this(domain, MechanismType.CCG);
    }

    public CCAuction(Domain domain, MechanismType mechanismType) {
        this(domain, mechanismType, false);
    }

    public CCAuction(Domain domain, MechanismFactory mechanismType, Prices currentPrices) {
        this(domain, mechanismType, false);
        this.currentPrices = currentPrices;
    }

    public CCAuction(Domain domain, MechanismFactory mechanismType, boolean proposeStartingPrices) {
        super(domain, mechanismType);
        setMaxRounds(100);
        if (proposeStartingPrices) {
            this.currentPrices = getDomain().proposeStartingPrices();
        } else {
            this.currentPrices = new LinearPrices(getDomain().getGoods());
        }
    }

    @Override
    public int allowedNumberOfBids() {
        if (!clockPhaseCompleted) return 1;
        SupplementaryRound next = supplementaryRoundQueue.peek();
        return next == null ? 0 : next.getNumberOfSupplementaryBids();
    }

    public ImmutableList<SupplementaryRound> getSupplementaryRounds() {
        return ImmutableList.copyOf(supplementaryRounds);
    }

    public void addSupplementaryRound(SupplementaryRound supplementaryRound) {
        supplementaryRounds.add(supplementaryRound);
        supplementaryRoundQueue.add(supplementaryRound);
    }

    /**
     * Overrides the default method to have mechanism results only based on each round's bids
     */
    @Override
    public MechanismResult getAuctionResultAtRound(int index) {
        if (getBidsAt(index).isEmpty()) return MechanismResult.NONE;
        if (getRound(index).getMechanismResult() == null) {
            getRound(index).setMechanismResult(getMechanismType().getMechanism(getBidsAt(index)).getMechanismResult());
        }
        return getRound(index).getMechanismResult();
    }

    /**
     * This is a shortcut to finish all rounds & calculate the final result
     */
    @Override
    public MechanismResult getMechanismResult() {
        log.info("Finishing all rounds...");
        while (!finished()) {
            nextRound();
        }
        log.info("Collected all bids. Running {} Auction to determine allocation & payments.", getMechanismType());
        return getMechanismType().getMechanism(getAggregatedBidsAt(rounds.size() - 1)).getMechanismResult();
    }

    public CCARound.Type getCurrentRoundType() {
        if (clockPhaseCompleted) return SUPPLEMENTARY;
        return CLOCK;
    }

    @Override
    public boolean currentPhaseFinished() {
        if (rounds.isEmpty()) return false;
        CCARound lastRound = (CCARound) rounds.get(rounds.size() - 1);
        if (CLOCK.equals(lastRound.getType()) && clockPhaseCompleted) {
            return true;
        } else {
            return finished();
        }
    }

    @Override
    public void closeRound() {
        Preconditions.checkState(!finished());
        Bids bids = current.getBids();
        Preconditions.checkArgument(getDomain().getBidders().containsAll(bids.getBidders()));
        Preconditions.checkArgument(getDomain().getGoods().containsAll(bids.getGoods()));
        int roundNumber = rounds.size() + 1;
        CCARound round;
        if (clockPhaseCompleted) {
            round = new CCARound(roundNumber, bids, getCurrentPrices(), SUPPLEMENTARY);
            supplementaryRoundQueue.poll();
        } else {
            round = new CCARound(roundNumber, bids, getCurrentPrices());
        }
        // if (current.hasMechanismResult()) {
        //     round.setMechanismResult(current.getMechanismResult());
        // }
        rounds.add(round);
        current = new AuctionRoundBuilder(getMechanismType());
        updatePrices();
    }

    @Override
    public boolean finished() {
        return super.finished() || clockPhaseCompleted && !hasNextSupplementaryRound();
    }

    @Override
    public Bid proposeBid(Bidder bidder) {
        Bid bid = super.proposeBid(bidder);
        if (CLOCK.equals(getCurrentRoundType())) {
            Set<BundleBid> bundleBids = bid.getBundleBids().stream()
                    .map(bb -> new BundleBid(getCurrentPrices().getPrice(bb.getBundle()).getAmount(), bb.getBundle(), bb.getId()))
                    .collect(Collectors.toSet());
            bid = new Bid(bundleBids);
        }
        return bid;
    }

    private void updatePrices() {
        Map<Good, Integer> demand = new HashMap<>();
        getDomain().getGoods().forEach(good -> demand.put(good, getLatestBids().getDemand(good)));
        Prices updatedPrices = priceUpdater.updatePrices(getCurrentPrices(), demand);
        if (getCurrentPrices().equals(updatedPrices) || getNumberOfRounds() >= getMaxRounds()) {
            clockPhaseCompleted = true;
            return;
        }
        currentPrices = updatedPrices;
    }

    @Override
    public void nextRound() {
        List<Bidder> biddersToQuery = getDomain().getBidders().stream().filter(b -> !current.getBids().getBidders().contains(b)).collect(Collectors.toList());
        if (!clockPhaseCompleted) {
            ClockPhaseBidCollector collector = new ClockPhaseBidCollector(getNumberOfRounds() + 1, getCurrentPrices(), biddersToQuery);
            log.debug("Starting clock round {}...", getNumberOfRounds() + 1);
            submitBids(collector.collectBids());
        } else {
            if (supplementaryRoundQueue.isEmpty()) {
                log.warn("No supplementary round found to run");
                return;
            }
            SupplementaryRound supplementaryRound = supplementaryRoundQueue.peek();
            SupplementaryBidCollector collector = new SupplementaryBidCollector(getNumberOfRounds() + 1, biddersToQuery, supplementaryRound);
            log.debug("Starting supplementary round '{}'...", collector);
            submitBids(collector.collectBids());
        }
        closeRound();
    }

    public boolean hasNextSupplementaryRound() {
        return !supplementaryRoundQueue.isEmpty();
    }

    @Override
    public void resetToRound(int index) {
        CCARound round = (CCARound) getRound(index);
        currentPrices = round.getPrices();
        if (SUPPLEMENTARY.equals(round.getType())) {
            CCARound previous = (CCARound) getRound(index - 1);
            Preconditions.checkState(CLOCK.equals(previous.getType()),
                    "Currently, the implementation does not allow to reset to another supplementary round than the first one.");
            clockPhaseCompleted = true;
            supplementaryRoundQueue = new LinkedList<>(supplementaryRounds);
        } else {
            clockPhaseCompleted = false;
            supplementaryRoundQueue = new LinkedList<>(supplementaryRounds);
        }
        super.resetToRound(index);
    }

}
