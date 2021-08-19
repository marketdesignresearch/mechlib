package org.marketdesignresearch.mechlib.mechanism.auctions.mlca;

import java.math.BigDecimal;
import java.util.Set;

import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBids;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionPhase;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases.BoundMLQueryWithMRPARPhase;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases.BoundRandomQueryPhase;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases.ConvergencePhase;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases.MLQueryPhase;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases.RandomQueryPhase;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases.RefinementPhase;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr.BoundDistributedSVR;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr.SupportVectorSetup;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRuleGenerator;

import lombok.extern.slf4j.Slf4j;

/**
 * The machine learning-powered iterative combinatorial auction with interval
 * bidding by Beyeler et. al. (2021).
 * 
 * @author Manuel Beyeler
 */
@Slf4j
public class iMLCAuction extends Auction<BundleBoundValueBids> {

	private static final boolean DEFAULT_REFINE_MARGINAL_ECONOMIES = false;
	private static final boolean DEFAULT_INTERMEDIATE_REFINEMENTS = false;

	/**
	 * @param domain                       the domain
	 * @param outcomeRule                  the outcome rule
	 * @param numberOfInitialRandomQueries number of initial queries per bidder in
	 *                                     the first phase (Q^init in Beyeler et.
	 *                                     al. (2021))
	 * @param maxQueries                   maximum number of queries per bidder in
	 *                                     the second phase (Q^max in Beyeler et.
	 *                                     al. (2021))
	 * @param marginalQueriesPerRound      number of marginal queries per round
	 *                                     (equals Q^round -1 in Beyeler et. al.
	 *                                     (2021), note that Q^round also includes
	 *                                     the query for the main economy)
	 * @param svrSetup                     the support vector setup
	 * @param seed                         the seed to sample random bundles in the
	 *                                     first phase
	 * @param timeLimit                    the time limit for the SVR WDP in the
	 *                                     second phase in seconds
	 * @param convergenceEpsilon           \epsion^stop in Beyeler et. al. (2021)
	 */
	public iMLCAuction(Domain domain, OutcomeRuleGenerator outcomeRule, int numberOfInitialRandomQueries,
			int maxQueries, int marginalQueriesPerRound, SupportVectorSetup svrSetup, Long seed, double timeLimit,
			BigDecimal convergenceEpsilon) {
		this(domain, outcomeRule, numberOfInitialRandomQueries, maxQueries, marginalQueriesPerRound,
				new BoundDistributedSVR(svrSetup), seed, timeLimit, convergenceEpsilon);
	}

	public iMLCAuction(Domain domain, OutcomeRuleGenerator outcomeRule, int numberOfInitialRandomQueries,
			int maxQueries, int marginalQueriesPerRound, MachineLearningComponent<BundleBoundValueBids> mlComponent,
			Long seed, double timeLimit, BigDecimal convergenceEpsilon) {
		this(domain, outcomeRule, new BoundRandomQueryPhase(numberOfInitialRandomQueries),
				new BoundMLQueryWithMRPARPhase(mlComponent, maxQueries, marginalQueriesPerRound,
						DEFAULT_REFINE_MARGINAL_ECONOMIES, DEFAULT_INTERMEDIATE_REFINEMENTS, timeLimit),
				new ConvergencePhase(marginalQueriesPerRound + 1, convergenceEpsilon), seed);
	}

	public iMLCAuction(Domain domain, OutcomeRuleGenerator outcomeRule,
			RandomQueryPhase<BundleBoundValueBids> initialPhase, MLQueryPhase<BundleBoundValueBids> mlPhase,
			RefinementPhase refinement, Long seed) {
		super(domain, outcomeRule, initialPhase, seed);
		this.addAuctionPhase(mlPhase);
		this.addAuctionPhase(refinement);
		this.setMaxRounds(1000);
	}

	public iMLCAuction(Domain domain, OutcomeRuleGenerator outcomeRule,
			RandomQueryPhase<BundleBoundValueBids> initialPhase, MLQueryPhase<BundleBoundValueBids> mlPhase,
			ConvergencePhase refinement, Long seed) {
		super(domain, outcomeRule, initialPhase, seed);
		this.addAuctionPhase(mlPhase);
		this.addAuctionPhase(refinement);
		this.setMaxRounds(1000);
	}

	public AuctionPhase<?> getPhaseNr(int i) {
		return this.phases.get(i);
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
