package org.marketdesignresearch.mechlib.mechanism.auctions.cca;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.mechanism.auctions.ExactValueAuction;
import org.marketdesignresearch.mechlib.mechanism.auctions.cca.priceupdate.PriceUpdater;
import org.marketdesignresearch.mechlib.mechanism.auctions.cca.supplementaryphase.SupplementaryPhase;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRuleGenerator;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * An Implementation of the well-known Combinatorial Clock Auction.
 * By default it has only a clock phase and can be extended with different
 * SupplementaryRounds ({@link SupplementaryPhase}).
 * 
 * Initial prices for the clock phase are obtained from the domain 
 * ({@link Domain#proposeStartingPrices()}) or set to 0 for all goods.
 * ({@link #CCAuction(Domain, OutcomeRuleGenerator, boolean)})
 * 
 * PriceUpdates in the clock round can be configured by using a decent
 * {@link PriceUpdater}
 * 
 * @author Manuel Beyeler
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class CCAuction extends ExactValueAuction {

	/**
	 * Creates a new CCA auction for the given domain.
	 * Setting initial prices to 0 and using the default 
	 * PriceUpater of the {@link CCAClockPhase}
	 * 
	 * Default Outcomerule is {@link OutcomeRuleGenerator#CCG}.
	 */
	public CCAuction(Domain domain) {
		this(domain, OutcomeRuleGenerator.CCG);
	}

	/**
	 * Creates a new CCA auction for the given domain.
	 * Initial prices are set to 0 and the given {@link PriceUpdater}
	 * is used.
	 */
	public CCAuction(Domain domain, PriceUpdater priceUpdater) {
		this(domain, OutcomeRuleGenerator.CCG, priceUpdater);
	}

	/**
	 * Creates a new CCA auction for the given domain.
	 * Initial prices are set to 0.
	 * Default Price Updater of {@link CCAClockPhase} is used.
	 */
	public CCAuction(Domain domain, OutcomeRuleGenerator outcomeRuleGenerator) {
		this(domain, outcomeRuleGenerator, false);
	}

	/**
	 * Creates a new CCA auction with initial prices set to 0.
	 */
	public CCAuction(Domain domain, OutcomeRuleGenerator outcomeRuleGenerator, PriceUpdater priceUpdater) {
		this(domain, outcomeRuleGenerator, false, priceUpdater);
	}

	/**
	 * Creates a new CCA auction with the default {@link PriceUpdater} of 
	 * the {@link CCAClockPhase}.
	 * 
	 * @param currentPrices initial prices
	 */
	public CCAuction(Domain domain, OutcomeRuleGenerator mechanismType, Prices currentPrices) {
		super(domain, mechanismType, new CCAClockPhase(currentPrices), null);
	}

	/**
	 * Creates a new CCAuction for the given domain.
	 * @param currentPrices initial prices
	 */
	public CCAuction(Domain domain, OutcomeRuleGenerator mechanismType, Prices currentPrices,
			PriceUpdater priceUpdater) {
		super(domain, mechanismType, new CCAClockPhase(currentPrices, priceUpdater), null);
	}

	/**
	 * Creates a CCAuction for the given domain.
	 * @param proposeStartingPrices if set to true the proposed prices of {@link Domain#proposeStartingPrices()} are used. Otherwiese prices are set to 0.
	 */
	public CCAuction(Domain domain, OutcomeRuleGenerator mechanismType, boolean proposeStartingPrices) {
		super(domain, mechanismType, new CCAClockPhase(domain, proposeStartingPrices), null);
	}

	/**
	 * Creates a CCAuction for the given domain.
	 * @param proposeStartingPrices if set to true the proposed prices of {@link Domain#proposeStartingPrices()} are used. Otherwiese prices are set to 0.
	 */
	public CCAuction(Domain domain, OutcomeRuleGenerator mechanismType, boolean proposeStartingPrices,
			PriceUpdater priceUpdater) {
		super(domain, mechanismType, new CCAClockPhase(domain, proposeStartingPrices, priceUpdater), null);
	}

	/**
	 * Adds a new supplementary phase to the end of the auction.
	 * Note that you can add multiple supplementary rounds to an auction
	 * @param supplementaryRound the supplementary phase to add
	 */
	public void addSupplementaryRound(SupplementaryPhase supplementaryRound) {
		this.addAuctionPhase(supplementaryRound);
	}

	/**
	 * Overrides the default method to have outcomes only based on each round's bids
	 */
	@Override
	public Outcome getOutcomeAtRound(OutcomeRuleGenerator generator, int index) {
		if (getBidsAt(index).isEmpty())
			return Outcome.NONE;
		return generator.getOutcomeRule(getBidsAt(index), getMipInstrumentation()).getOutcome();
	}

	/**
	 * This is a shortcut to finish all rounds and calculate the final result
	 */
	@Override
	public Outcome getOutcome(OutcomeRuleGenerator generator) {
		log.info("Finishing all rounds...");
		while (!finished()) {
			advanceRound();
		}
		log.info("Collected all bids. Running {} Auction to determine allocation & payments.", generator);
		return generator.getOutcomeRule(getAggregatedBidsAt(rounds.size() - 1), getMipInstrumentation()).getOutcome();
	}

	public String getCurrentRoundType() {
		return this.getCurrentPhase().getType();
	}

	/**
	 * @return true if the clock phase has completed
	 */
	public boolean isClockPhaseCompleted() {
		return this.currentPhaseNumber > 0;
	}

	public boolean currentPhaseFinished() {
		return this.getCurrentPhase().phaseFinished(this);
	}

	public boolean hasNextSupplementaryRound() {
		return !this.finished() && this.phases.size() > 1;
	}

	/**
	 * Resets the auction to the end of the clock phase if the clock phase
	 * has already finished and replaces all supplementary phases with the given phase
	 * @param newSuppPhase the new supplementary phase
	 */
	public void replaceSupplementaryPhases(SupplementaryPhase newSuppPhase) {
		this.phases = Stream.of(this.phases.get(0)).collect(Collectors.toList());
		this.phases.add(newSuppPhase);
		this.resetToRound(this.rounds.stream().filter(r -> r.getAuctionPhaseNumber() == 0).map(r -> r.getRoundNumber())
				.reduce(Integer::max).orElse(-1) + 1);
	}
}
