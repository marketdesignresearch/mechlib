package org.marketdesignresearch.mechlib.mechanism.auctions;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.instrumentation.AuctionInstrumentationable;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentation;
import org.marketdesignresearch.mechlib.mechanism.Mechanism;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.Interaction;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRuleGenerator;
import org.springframework.data.annotation.PersistenceConstructor;

import com.google.common.base.Preconditions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;


@ToString(callSuper = true) 
@EqualsAndHashCode(callSuper = true)
@Slf4j
public abstract class Auction<BB extends BundleValueBids<?>> extends Mechanism implements AuctionInstrumentationable {

    private static final int DEFAULT_MAX_ROUNDS = 0;

    @Getter
    private final Domain domain;
    @Getter
    private final OutcomeRuleGenerator outcomeRuleGenerator;

    /**
     * Maximum number of rounds in this auction
     * 
     * If maxRounds equals to 0, the number of rounds is infinite and the auction
     * terminates after all auction phases have completed successfully.
     */
    @Getter @Setter
    private int maxRounds = DEFAULT_MAX_ROUNDS;

    protected List<AuctionRound<BB>> rounds = new ArrayList<>();
    
    @Getter
    protected int currentPhaseNumber = 0;
    @Getter
    protected int currentPhaseRoundNumber = 0;
    protected List<AuctionPhase<BB>> phases = new ArrayList<>();

    protected AuctionRoundBuilder<BB> current;

    public Auction(Domain domain, OutcomeRuleGenerator outcomeRuleGenerator, AuctionPhase<BB> firstPhase) {
        super();
        this.domain = domain;
        this.outcomeRuleGenerator = outcomeRuleGenerator;
        this.phases.add(firstPhase);
        this.prepareNextAuctionRoundBuilder();
        // TODO
        //current.setMipInstrumentation(getMipInstrumentation());
    }
    
    @PersistenceConstructor
    protected Auction(Domain domain, OutcomeRuleGenerator outcomeRuleGenerator, AuctionRoundBuilder<BB> current) {
    	this.domain = domain;
    	this.outcomeRuleGenerator = outcomeRuleGenerator;
    	this.current = current;
    	current.setAuction(this);
    }
    
    protected boolean addAuctionPhase(AuctionPhase<BB> auctionPhase) {
    	Preconditions.checkArgument(!this.finished());
    	return this.phases.add(auctionPhase);
    }
    
    protected AuctionPhase<BB> getCurrentPhase() {
    	return this.phases.get(this.currentPhaseNumber);
    }
    
    public Interaction getCurrentInteraction(Bidder b) {
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
        return this.current == null;
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
    	
        AuctionRound<BB> round = this.current.build();
        getAuctionInstrumentation().postRound(round);
        rounds.add(round);
        prepareNextAuctionRoundBuilder();
    }

	protected void prepareNextAuctionRoundBuilder() {
		if((this.phases.size() == this.currentPhaseNumber+1 && this.getCurrentPhase().phaseFinished(this)) || (maxRounds > 0 && getNumberOfRounds() >= maxRounds )) {
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


    public BB getAggregatedBidsAt(int round) {
        Preconditions.checkArgument(round >= 0 && round < rounds.size());
        return rounds.subList(0, round + 1).stream()
                .map(AuctionRound::getBids)
                .reduce(this::join).orElse(this.createEmptyBids());
    }

    public BB getBidsAt(int round) {
        Preconditions.checkArgument(round >= 0 && round < rounds.size());
        return rounds.get(round).getBids();
    }

    public BB getLatestAggregatedBids() {
        if (rounds.size() == 0) return this.createEmptyBids();
        return getAggregatedBidsAt(rounds.size() - 1);
    }

    public BB getLatestBids() {
        if (rounds.size() == 0) return this.createEmptyBids();
        return getBidsAt(rounds.size() - 1);
    }
    
    protected abstract BB join(BB b1, BB b2);
    protected abstract BB createEmptyBids();
    /**
     * Get the round with the given index. Note that the first round has index 0
     * 
     * @param index The index of the requested round.
     * @return the round if it exists
     */
    public AuctionRound<BB> getRound(int index) {
        Preconditions.checkArgument(index >= 0 && index < rounds.size());
        return rounds.get(index);
    }
    
    public AuctionRound<BB> getLastRound() {
    	return this.getRound(this.getNumberOfRounds()-1);
    }

    /**
     * Get the number of complete rounds. If the auction is not finished
     * round getNumberOfRounds() + 1 is running at the moment.
     * 
     * @return number of completed rounds
     */
    public int getNumberOfRounds() {
        return rounds.size();
    }

    
    /**
     * Reset the auction such that it is in the state of executing the given round. 
     * (I.e. {@link #getNumberOfRounds()} will return round-1 after this method has executed.
     * 
     * @param round the round that should be executed next by this auction
     */
    public void resetToRound(int round) {
        Preconditions.checkArgument(round >= 1 && round <= rounds.size()+1);
        rounds = rounds.subList(0, round-1);
        if(round > 0) {
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
    	return this.getOutcomeAtRound(this.getOutcomeRuleGenerator(),index);
    }
    
    public Outcome getOutcomeAtRound(OutcomeRuleGenerator generator, int index) {
    	return generator.getOutcomeRule(getAggregatedBidsAt(index), getMipInstrumentation()).getOutcome();
    }
    
    public int getMaximumSubmittedBids() {
    	return this.getDomain().getBidders().stream().map(b -> this.rounds.stream().map(r -> r.getBids().getBid(b).getBundleBids().size()).reduce(Integer::sum).orElse(0)).reduce(Integer::max).orElse(0);
    }

    /**
     * By default, the outcome is the last round's outcome, because the bids are aggregated.
     * This has to be overridden, e.g., for the sequential auction
     */
    @Override
    public Outcome getOutcome() {
        return this.getOutcome(this.getOutcomeRuleGenerator());
    }
    
    public Outcome getOutcome(OutcomeRuleGenerator generator) {
    	if (rounds.size() == 0) return Outcome.NONE;
        return getOutcomeAtRound(generator,rounds.size() - 1);
    }
    
    /**
     * 
     * @param i queried phase number (starting from 0)
     * @return
     */
    public String getPhaseType(int i) {
    	return this.phases.get(i).getType();
    }
    
    public int getNumberOfPhases() {
    	return this.phases.size();
    }

    // region instrumentation
    @Override
    public void setMipInstrumentation(MipInstrumentation mipInstrumentation) {
        super.setMipInstrumentation(mipInstrumentation);
        this.domain.setMipInstrumentation(mipInstrumentation);
    }
    // endregion
}
