package org.marketdesignresearch.mechlib.mechanism.auctions.mlca;

import java.util.Set;

import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBids;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases.BoundMLQueryWithMRPARPhase;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases.BoundRandomQueryPhase;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases.MLQueryPhase;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases.RandomQueryPhase;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases.RefinementPhase;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr.BoundDistributedSVR;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr.SupportVectorSetup;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRuleGenerator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class iMLCAuction extends Auction<BundleBoundValueBids> {

	private static final boolean DEFAULT_REFINE_MARGINAL_ECONOMIES = false;
	private static final boolean DEFAULT_INTERMEDIATE_REFINEMENTS = false;
	
	public iMLCAuction(Domain domain, OutcomeRuleGenerator outcomeRule, int numberOfInitialRandomQueries,
			int maxQueries, int marginalQueriesPerRound, SupportVectorSetup svrSetup, Long seed, double timeLimit) {
		this(domain, outcomeRule, numberOfInitialRandomQueries, maxQueries, marginalQueriesPerRound,
				new BoundDistributedSVR(svrSetup), DEFAULT_REFINE_MARGINAL_ECONOMIES, DEFAULT_INTERMEDIATE_REFINEMENTS,
				seed, timeLimit);
	}

	public iMLCAuction(Domain domain, OutcomeRuleGenerator outcomeRule, int numberOfInitialRandomQueries,
			int maxQueries, int marginalQueriesPerRound, SupportVectorSetup svrSetup, boolean refineMarginalEconomies,
			boolean intermediateRefinement, Long seed, double timeLimit) {
		this(domain, outcomeRule, numberOfInitialRandomQueries, maxQueries, marginalQueriesPerRound,
				new BoundDistributedSVR(svrSetup), refineMarginalEconomies, intermediateRefinement, seed, timeLimit);
	}

	public iMLCAuction(Domain domain, OutcomeRuleGenerator outcomeRule, int numberOfInitialRandomQueries,
			int maxQueries, int marginalQueriesPerRound, MachineLearningComponent<BundleBoundValueBids> mlComponent,
			boolean refineMarginalEconomies, boolean intermediateRefinement, Long seed, double timeLimit) {
		this(domain, outcomeRule, new BoundRandomQueryPhase(numberOfInitialRandomQueries),
				new BoundMLQueryWithMRPARPhase(mlComponent, maxQueries, marginalQueriesPerRound,
						refineMarginalEconomies, intermediateRefinement, timeLimit),
				new RefinementPhase(refineMarginalEconomies, timeLimit), seed);
	}

	public iMLCAuction(Domain domain, OutcomeRuleGenerator outcomeRule,
			RandomQueryPhase<BundleBoundValueBids> initialPhase, MLQueryPhase<BundleBoundValueBids> mlPhase,
			RefinementPhase refinement, Long seed) {
		super(domain, outcomeRule, initialPhase, seed);
		this.addAuctionPhase(mlPhase);
		this.addAuctionPhase(refinement);
		this.setMaxRounds(1000);
	}

	
	public RefinementPhase getRefinementPhase() {
		return (RefinementPhase) this.phases.get(2);
	}
	
	/**
	 * This is a shortcut to finish all rounds and calculate the final result
	 */
	@Override
	public Outcome getOutcome() {
		log.info("Finishing all rounds...");
		while (!finished()) {
			advanceRound();
		}
		log.info("Collected all bids. Running {} Auction to determine allocation & payments.",
				getOutcomeRuleGenerator());
		return getOutcomeAtRound(rounds.size() - 1);
	}

	@Override
	protected BundleBoundValueBids join(BundleBoundValueBids b1, BundleBoundValueBids b2) {
		return b1.join(b2);
	}

	@Override
	protected BundleBoundValueBids createEmptyBids() {
		return new BundleBoundValueBids();
	}

	@Override
	public int getMaximumSubmittedBids() {
		return this.getLatestAggregatedBids().getBids().stream().map(BundleBoundValueBid::getBundleBids)
				.mapToInt(Set::size).max().orElse(0);
	}
}
