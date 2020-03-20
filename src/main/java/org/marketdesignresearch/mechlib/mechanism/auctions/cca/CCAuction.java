package org.marketdesignresearch.mechlib.mechanism.auctions.cca;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.ExactValueAuction;
import org.marketdesignresearch.mechlib.mechanism.auctions.cca.priceupdate.PriceUpdater;
import org.marketdesignresearch.mechlib.mechanism.auctions.cca.supplementaryphase.SupplementaryPhase;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRuleGenerator;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class CCAuction extends ExactValueAuction {

    public CCAuction(Domain domain) {
        this(domain, OutcomeRuleGenerator.CCG);
    }

    public CCAuction(Domain domain, OutcomeRuleGenerator outcomeRuleGenerator) {
        this(domain, outcomeRuleGenerator, false);
    }

    public CCAuction(Domain domain, OutcomeRuleGenerator mechanismType, Prices currentPrices) {
        super(domain,mechanismType,new CCAClockPhase(currentPrices));
        setMaxRounds(100);
    }

    public CCAuction(Domain domain, OutcomeRuleGenerator mechanismType, boolean proposeStartingPrices) {
        super(domain, mechanismType, new CCAClockPhase(domain, proposeStartingPrices));
        setMaxRounds(100);
    }

    public void addSupplementaryRound(SupplementaryPhase supplementaryRound) {
        this.addAuctionPhase(supplementaryRound);
    }
    
    public void setPriceUpdater(PriceUpdater updater) {
    	((CCAClockPhase)this.phases.get(0)).setPriceUpdater(updater);
    }

    /**
     * Overrides the default method to have outcomes only based on each round's bids
     * TODO ?? Why ??
     */
    @Override
    public Outcome getOutcomeAtRound(int index) {
        if (getBidsAt(index).isEmpty()) return Outcome.NONE;
        return getOutcomeRuleGenerator().getOutcomeRule(getBidsAt(index), getMipInstrumentation()).getOutcome();
        /*
        if (getRound(index).getOutcome() == null) {
            getRound(index).setOutcome(getOutcomeRuleGenerator().getOutcomeRule(getBidsAt(index), getMipInstrumentation()).getOutcome());
        }
        return getRound(index).getOutcome();
        */
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
    	return this.getCurrentPhase().getType();
    }

	public boolean isClockPhaseCompleted() {
		return this.currentPhaseNumber > 0;
	}

	public boolean currentPhaseFinished() {
		return this.getNumberOfRounds() != 0 && this.getLastRound().getAuctionPhaseNumber() != this.currentPhaseNumber;
	}

	public boolean hasNextSupplementaryRound() {
		return !this.finished() && this.phases.size() > 1;
	}
	
	public void replaceSupplementaryPhases(SupplementaryPhase newSuppPhase) {
		this.phases = Stream.of(this.phases.get(0)).collect(Collectors.toList());
		this.phases.add(newSuppPhase);
		this.resetToRound(this.rounds.stream().filter(r -> r.getAuctionPhaseNumber() == 0).map(r -> r.getRoundNumber()).reduce(Integer::max).orElse(-1)+1);
	}
}
