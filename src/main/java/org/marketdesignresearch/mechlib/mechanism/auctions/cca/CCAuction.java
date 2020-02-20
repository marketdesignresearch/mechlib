package org.marketdesignresearch.mechlib.mechanism.auctions.cca;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bid.Bid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.price.LinearPrices;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRound;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRoundBuilder;
import org.marketdesignresearch.mechlib.mechanism.auctions.cca.bidcollection.supplementaryround.SupplementaryRound;
import org.marketdesignresearch.mechlib.mechanism.auctions.cca.priceupdate.PriceUpdater;
import org.marketdesignresearch.mechlib.mechanism.auctions.cca.priceupdate.SimpleRelativePriceUpdate;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRuleGenerator;
import org.springframework.data.annotation.PersistenceConstructor;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@ToString(callSuper = true) @EqualsAndHashCode(callSuper = true)
@Slf4j
public class CCAuction extends Auction<BundleValuePair> {

    @Getter
    private Prices currentPrices;

    @Setter
    private PriceUpdater priceUpdater = new SimpleRelativePriceUpdate();
    private final List<SupplementaryRound> supplementaryRounds;
    private LinkedList<SupplementaryRound> supplementaryRoundQueue = new LinkedList<>();

    @Getter
    private boolean clockPhaseCompleted = false;

    public CCAuction(Domain domain) {
        this(domain, OutcomeRuleGenerator.CCG);
    }

    public CCAuction(Domain domain, OutcomeRuleGenerator outcomeRuleGenerator) {
        this(domain, outcomeRuleGenerator, false);
    }

    public CCAuction(Domain domain, OutcomeRuleGenerator mechanismType, Prices currentPrices) {
        this(domain, mechanismType, false);
        this.currentPrices = currentPrices;
    }

    public CCAuction(Domain domain, OutcomeRuleGenerator mechanismType, boolean proposeStartingPrices) {
        super(domain, mechanismType);
        this.supplementaryRounds = new ArrayList<>();
        setMaxRounds(100);
        if (proposeStartingPrices) {
            this.currentPrices = getDomain().proposeStartingPrices();
        } else {
            this.currentPrices = new LinearPrices(getDomain().getGoods());
        }
        this.buildInteractions();
    }

    @PersistenceConstructor
    private CCAuction(Domain domain, OutcomeRuleGenerator outcomeRuleGenerator, Prices currentPrices, PriceUpdater priceUpdater, List<SupplementaryRound> supplementaryRounds, LinkedList<SupplementaryRound> supplementaryRoundQueue, boolean clockPhaseCompleted) {
        super(domain, outcomeRuleGenerator);
        this.currentPrices = currentPrices;
        this.priceUpdater = priceUpdater;
        this.supplementaryRounds = supplementaryRounds;
        this.supplementaryRoundQueue = supplementaryRoundQueue;
        this.clockPhaseCompleted = clockPhaseCompleted;
        // TODO handle interactions
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
     * Overrides the default method to have outcomes only based on each round's bids
     */
    @Override
    public Outcome getOutcomeAtRound(int index) {
        if (getBidsAt(index).isEmpty()) return Outcome.NONE;
        if (getRound(index).getOutcome() == null) {
            getRound(index).setOutcome(getOutcomeRuleGenerator().getOutcomeRule(getBidsAt(index), getMipInstrumentation()).getOutcome());
        }
        return getRound(index).getOutcome();
    }

    /**
     * This is a shortcut to finish all rounds & calculate the final result
     */
    @Override
    public Outcome getOutcome() {
        log.info("Finishing all rounds...");
        while (!finished()) {
            advanceRound();
        }
        log.info("Collected all bids. Running {} Auction to determine allocation & payments.", getOutcomeRuleGenerator());
        return getOutcomeRuleGenerator().getOutcomeRule(getAggregatedBidsAt(rounds.size() - 1), getMipInstrumentation()).getOutcome();
    }

    public String getCurrentRoundType() {
        if (clockPhaseCompleted) return "SUPPLEMENTARY";
        return "CLOCK";
    }

    @Override
    public boolean currentPhaseFinished() {
        if (rounds.isEmpty()) return false;
        AuctionRound<BundleValuePair> lastRound = rounds.get(rounds.size() - 1);
        if (lastRound instanceof CCAClockRound && clockPhaseCompleted) {
            return true;
        } else {
            return finished();
        }
    }

    @Override
    public void closeRound() {
        Preconditions.checkState(!finished());
        BundleValueBids<BundleValuePair> bids = current.getBids();
        Preconditions.checkArgument(getDomain().getBidders().containsAll(bids.getBidders()));
        Preconditions.checkArgument(getDomain().getGoods().containsAll(bids.getGoods()));
        int roundNumber = rounds.size() + 1;
        AuctionRound<BundleValuePair> round;
        if (clockPhaseCompleted) {
            round = new CCASupplementaryRound(roundNumber, bids, getCurrentPrices());
            supplementaryRoundQueue.poll();
        } else {
            round = new CCAClockRound(roundNumber, bids, getCurrentPrices(), getDomain().getGoods());
        }
        // if (current.hasOutcome()) {
        //     round.setOutcome(current.getOutcome());
        // }
        getAuctionInstrumentation().postRound(round);
        rounds.add(round);
        current = new AuctionRoundBuilder<BundleValuePair>(getOutcomeRuleGenerator());
        current.setMipInstrumentation(getMipInstrumentation());
        updatePrices();
        buildInteractions();
    }

    @Override
    public boolean finished() {
        return super.finished() || clockPhaseCompleted && !hasNextSupplementaryRound();
    }
    
    @SuppressWarnings("unchecked")
	private void buildInteractions() {
    	Map<UUID, BundleValuePairTransformable<Bid,BundleValuePair>> currentInteractions = new HashMap<UUID, BundleValuePairTransformable<Bid,BundleValuePair>>();
    	if(!clockPhaseCompleted) {
    		for(Bidder b : this.getDomain().getBidders()) {
    			currentInteractions.put(b.getId(), (BundleValuePairTransformable)new DefaultDemandQueryInteraction(b.getId(), this.currentPrices));
    		}
    	}
    	this.setCurrentInteractions(currentInteractions);
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

    public boolean hasNextSupplementaryRound() {
        return !supplementaryRoundQueue.isEmpty();
    }

    @Override
    public void resetToRound(int index) {
        AuctionRound<BundleValuePair> round = getRound(index);
        currentPrices = round.getPrices();
        if (clockPhaseCompleted) {
            AuctionRound<BundleValuePair> previous = getRound(index - 1);
            Preconditions.checkState(previous instanceof CCAClockRound,
                    "Currently, the implementation does not allow to reset to another supplementary round than the first one.");
            if (round instanceof CCAClockRound) {
                clockPhaseCompleted = false;
            }
            supplementaryRoundQueue = new LinkedList<>(supplementaryRounds);
        } else {
            supplementaryRoundQueue = new LinkedList<>(supplementaryRounds);
        }
        super.resetToRound(index);
    }

}
