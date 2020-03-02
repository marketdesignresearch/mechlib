package org.marketdesignresearch.mechlib.mechanism.auctions;

import com.google.common.base.Preconditions;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.marketdesignresearch.mechlib.core.*;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.mechanism.Mechanism;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.Interaction;
import org.marketdesignresearch.mechlib.instrumentation.AuctionInstrumentationable;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentation;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRuleGenerator;
import org.springframework.data.annotation.PersistenceConstructor;

import java.util.*;


@ToString(callSuper = true) 
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class Auction<T extends BundleValuePair> extends Mechanism implements AuctionInstrumentationable {

    private static int DEFAULT_MAX_BIDS = 100;
    private static int DEFAULT_MANUAL_BIDS = 0;
    private static int DEFAULT_MAX_ROUNDS = 1;

    @Getter
    private final Domain domain;
    @Getter
    private final OutcomeRuleGenerator outcomeRuleGenerator;

    @Getter @Setter
    private int maxBids = DEFAULT_MAX_BIDS;
    @Getter @Setter
    private int manualBids = DEFAULT_MANUAL_BIDS;
    @Getter @Setter
    private int maxRounds = DEFAULT_MAX_ROUNDS;
    
    // TODO move to demand query strategy
    @Getter @Setter
    private double relativeDemandQueryTolerance = 0;
    @Getter @Setter
    private double absoluteDemandQueryTolerance = 0;
    @Getter @Setter
    private double demandQueryTimeLimit = -1;

    protected List<AuctionRound<T>> rounds = new ArrayList<>();
    
    @Getter
    protected int currentPhaseNumber = 0;
    @Getter
    protected int currentPhaseRoundNumber = 0;
    protected List<AuctionPhase<T>> phases = new ArrayList<>();

    protected AuctionRoundBuilder<T> current;

    public Auction(Domain domain, OutcomeRuleGenerator outcomeRuleGenerator, AuctionPhase<T> firstPhase) {
        super();
        this.domain = domain;
        this.outcomeRuleGenerator = outcomeRuleGenerator;
        this.phases.add(firstPhase);
        this.prepareNextAuctionRoundBuilder();
        // TODO
        //current.setMipInstrumentation(getMipInstrumentation());
    }
    
    @PersistenceConstructor
    protected Auction(Domain domain, OutcomeRuleGenerator outcomeRuleGenerator, AuctionRoundBuilder<T> current) {
    	this.domain = domain;
    	this.outcomeRuleGenerator = outcomeRuleGenerator;
    	this.current = current;
    	current.setAuction(this);
    }
    
    public boolean addAuctionPhase(AuctionPhase<T> auctionPhase) {
    	Preconditions.checkArgument(!this.finished());
    	return this.phases.add(auctionPhase);
    }
    
    protected AuctionPhase<T> getCurrentPhase() {
    	return this.phases.get(this.currentPhaseNumber);
    }
    
    public Interaction<T> getCurrentInteraction(Bidder b) {
    	return this.current.getInteractions().get(b.getId());
    }

    public Bidder getBidder(UUID id) {
        return domain.getBidders().stream().filter(b -> b.getId().equals(id)).findFirst().orElseThrow(NoSuchElementException::new);
    }

    public Good getGood(String name) {
        return domain.getGoods().stream().filter(b -> b.getName().equals(name)).findFirst().orElseThrow(NoSuchElementException::new);
    }

    public Good getGood(UUID id) {
        return domain.getGoods().stream().filter(b -> b.getUuid().equals(id)).findFirst().orElseThrow(NoSuchElementException::new);
    }

    public boolean finished() {
        return getNumberOfRounds() >= maxRounds || 
        		(this.phases.size() == this.currentPhaseNumber+1 && this.getCurrentPhase().phaseFinished(this));
    }
    
    /**
     * This fills up the not-yet-submitted bids and closes the round
     */
    public void advanceRound() {
        this.current.getInteractions().forEach((b,i) -> i.submitProposedBid());
        closeRound();
    }

    public void closeRound() {
    	Preconditions.checkState(!this.finished());
    	
        AuctionRound<T> round = this.current.build();
        getAuctionInstrumentation().postRound(round);
        rounds.add(round);
        prepareNextAuctionRoundBuilder();
    }

	protected void prepareNextAuctionRoundBuilder() {
		if(this.finished()) {
        	current = null;
        } else {
        	if(this.getCurrentPhase().phaseFinished(this)) {
        		this.currentPhaseNumber++;
        		this.currentPhaseRoundNumber = 0;
        	}
        	current = this.getCurrentPhase().createNextRoundBuilder(this);
        	this.currentPhaseRoundNumber++;
        }
	}
    

    public Outcome getTemporaryResult() {
        return current.computeTemporaryResult(this.outcomeRuleGenerator);
    }


    public BundleValueBids<T> getAggregatedBidsAt(int round) {
        Preconditions.checkArgument(round >= 0 && round < rounds.size());
        return rounds.subList(0, round + 1).stream()
                .map(AuctionRound::getBids)
                .reduce(new BundleValueBids<T>(), BundleValueBids::join);
    }

    public BundleValueBids<T> getBidsAt(int round) {
        Preconditions.checkArgument(round >= 0 && round < rounds.size());
        return rounds.get(round).getBids();
    }

    public BundleValueBid<T> getAggregatedBidsAt(Bidder bidder, int round) {
        Preconditions.checkArgument(round >= 0 && round < rounds.size());
        return rounds.subList(0, round + 1).stream()
                .map(AuctionRound::getBids)
                .map(bids -> bids.getBid(bidder))
                .reduce(new BundleValueBid<T>(), BundleValueBid::join);
    }

    public BundleValueBid<T> getBidsAt(Bidder bidder, int round) {
        return getBidsAt(round).getBid(bidder);
    }

    public BundleValueBids<T> getLatestAggregatedBids() {
        if (rounds.size() == 0) return new BundleValueBids<T>();
        return getAggregatedBidsAt(rounds.size() - 1);
    }

    public BundleValueBids<T> getLatestBids() {
        if (rounds.size() == 0) return new BundleValueBids<T>();
        return getBidsAt(rounds.size() - 1);
    }

    public BundleValueBid<T> getLatestAggregatedBids(Bidder bidder) {
        if (rounds.size() == 0) return new BundleValueBid<T>();
        return getAggregatedBidsAt(bidder, rounds.size() - 1);
    }

    public BundleValueBid<T> getLatestBids(Bidder bidder) {
        if (rounds.size() == 0) return new BundleValueBid<T>();
        return getBidsAt(bidder, rounds.size() - 1);
    }

    public AuctionRound<T> getRound(int index) {
        Preconditions.checkArgument(index >= 0 && index < rounds.size());
        return rounds.get(index);
    }
    
    public AuctionRound<T> getLastRound() {
    	return this.getRound(this.getNumberOfRounds()-1);
    }

    public int getNumberOfRounds() {
        return rounds.size();
    }

    public void resetToRound(int index) {
        Preconditions.checkArgument(index < rounds.size());
        rounds = rounds.subList(0, index);
        if(index > 0) {
        	this.currentPhaseNumber = this.getLastRound().getAuctionPhaseNumber();
        	this.currentPhaseRoundNumber = this.getLastRound().getAuctionPhaseRoundNumber();
        } else {
        	this.currentPhaseNumber = 0;
        	this.currentPhaseRoundNumber = 0;
        }
        this.prepareNextAuctionRoundBuilder();
    }

    /**
     * By default, the bids for the auction results are aggregated in each round
     * TODO: does it make sense to store outcomes in the auction rounds? rounds can be accessed directly and then some rounds contain outcomes some other don't
     */
    public Outcome getOutcomeAtRound(int index) {
    	return outcomeRuleGenerator.getOutcomeRule(getAggregatedBidsAt(index), getMipInstrumentation()).getOutcome();
    	/*
        if (getRound(index).getOutcome() == null) {
            getRound(index).setOutcome(outcomeRuleGenerator.getOutcomeRule(getAggregatedBidsAt(index), getMipInstrumentation()).getOutcome());
        }
        return getRound(index).getOutcome();
        */
    }

    /**
     * By default, the outcome is the last round's outcome, because the bids are aggregated.
     * This has to be overridden, e.g., for the sequential auction
     */
    @Override
    public Outcome getOutcome() {
        if (rounds.size() == 0) return Outcome.NONE;
        return getOutcomeAtRound(rounds.size() - 1);
    }

    // region instrumentation
    @Override
    public void setMipInstrumentation(MipInstrumentation mipInstrumentation) {
        super.setMipInstrumentation(mipInstrumentation);
        this.domain.setMipInstrumentation(mipInstrumentation);
    }
    // endregion
}
