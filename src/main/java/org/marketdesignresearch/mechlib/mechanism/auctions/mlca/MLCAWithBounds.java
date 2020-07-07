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
public class MLCAWithBounds extends Auction<BundleBoundValueBids>{	

	private static final boolean DEFAULT_REFINE_MARGINAL_ECONOMIES = false;
	private static final boolean DEFAULT_INTERMEDIATE_REFINEMENTS = false;
	
	public MLCAWithBounds(Domain domain, OutcomeRuleGenerator outcomeRule, long sampleSeed, int numberOfInitialRandomQueries, int maxQueries, int marginalQueriesPerRound, SupportVectorSetup svrSetup) {
		this(domain, outcomeRule, sampleSeed, numberOfInitialRandomQueries, maxQueries, marginalQueriesPerRound, new BoundDistributedSVR(svrSetup), DEFAULT_REFINE_MARGINAL_ECONOMIES, DEFAULT_INTERMEDIATE_REFINEMENTS);
	}
	
	public MLCAWithBounds(Domain domain, OutcomeRuleGenerator outcomeRule, long sampleSeed, int numberOfInitialRandomQueries, int maxQueries, int marginalQueriesPerRound, SupportVectorSetup svrSetup, boolean refineMarginalEconomies, boolean intermediateRefinement) {
		this(domain, outcomeRule, sampleSeed, numberOfInitialRandomQueries, maxQueries, marginalQueriesPerRound, new BoundDistributedSVR(svrSetup), refineMarginalEconomies, intermediateRefinement);
	}
	
	public MLCAWithBounds(Domain domain, OutcomeRuleGenerator outcomeRule, long sampleSeed, int numberOfInitialRandomQueries, int maxQueries, int marginalQueriesPerRound, MachineLearningComponent<BundleBoundValueBids> mlComponent, boolean refineMarginalEconomies, boolean intermediateRefinement) {
		this(domain,outcomeRule,new BoundRandomQueryPhase(sampleSeed, numberOfInitialRandomQueries), new BoundMLQueryWithMRPARPhase(mlComponent, sampleSeed+1, refineMarginalEconomies, intermediateRefinement), new RefinementPhase(sampleSeed+3, refineMarginalEconomies));
	}
	
	public MLCAWithBounds(Domain domain, OutcomeRuleGenerator outcomeRule, RandomQueryPhase<BundleBoundValueBids> initialPhase, MLQueryPhase<BundleBoundValueBids> mlPhase, RefinementPhase refinement) {
		super(domain,outcomeRule,initialPhase);
		this.addAuctionPhase(mlPhase);
		this.addAuctionPhase(refinement);
		this.setMaxRounds(1000);
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
		return this.getLatestAggregatedBids().getBids().stream().map(BundleBoundValueBid::getBundleBids).mapToInt(Set::size).max().orElse(0);
	}
}
