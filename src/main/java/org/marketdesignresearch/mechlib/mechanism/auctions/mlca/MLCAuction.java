package org.marketdesignresearch.mechlib.mechanism.auctions.mlca;

import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.mechanism.auctions.ExactValueAuction;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases.ExactMLQueryPhase;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases.ExactRandomQueryPhase;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases.MLQueryPhase;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases.RandomQueryPhase;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr.ExactDistributedSVR;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr.SupportVectorSetup;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRuleGenerator;

import lombok.extern.slf4j.Slf4j;

/**
 * The machine learning-powered iterative combinatorial auction by Brero et. al.
 * (2021).
 * 
 * SVR hyper parameters for GSVM, LSVM and MRVM are provided with the domains as
 * part of SATS.
 * 
 * You may also implement your own MachineLearningComponent (other than the
 * standard SVR).
 * 
 * @author Manuel Beyeler
 */
@Slf4j
public class MLCAuction extends ExactValueAuction {

	/**
	 * Creates a new MLCA auction.
	 * 
	 * @param domain                       the domain
	 * @param outcomeRule                  OutcomeRule that will be used to compute
	 *                                     the outcome
	 * @param numberOfInitialRandomQueries parameter Q_init from Brero et. al.
	 *                                     (2021).
	 * @param maxQueries                   Q_max from Brero et. al. (2021).
	 * @param marginalQueriesPerRound      (Q_round-1) from Brero et. al. (2021).
	 * @param svrSetup                     the setup for the support vector
	 *                                     regression
	 * @param seed                         a seed that is used to make random
	 *                                     choices in the auction (i.e. sample
	 *                                     random initial bundles)
	 */
	public MLCAuction(Domain domain, OutcomeRuleGenerator outcomeRule, int numberOfInitialRandomQueries, int maxQueries,
			int marginalQueriesPerRound, SupportVectorSetup svrSetup, Long seed) {
		this(domain, outcomeRule, numberOfInitialRandomQueries, maxQueries, marginalQueriesPerRound,
				new ExactDistributedSVR(svrSetup), seed);
	}

	/**
	 * Creates a new MLCA auction.
	 * 
	 * @param domain                       the domain
	 * @param outcomeRule                  OutcomeRule that will be used to compute
	 *                                     the outcome
	 * @param numberOfInitialRandomQueries parameter Q_init from Brero et. al.
	 *                                     (2020).
	 * @param maxQueries                   Q_max from Brero et. al. (2021).
	 * @param marginalQueriesPerRound      (Q_round-1) from Brero et. al. (2021).
	 * @param mlComponent                  a generic machine learning component that
	 *                                     will be used by the query module to infer
	 *                                     the optimal allocation
	 * @param seed                         a seed that is used to make random
	 *                                     choices in the auction (i.e. sample
	 *                                     random initial bundles)
	 */
	public MLCAuction(Domain domain, OutcomeRuleGenerator outcomeRule, int numberOfInitialRandomQueries, int maxQueries,
			int marginalQueriesPerRound, MachineLearningComponent<BundleExactValueBids> mlComponent, Long seed) {
		this(domain, outcomeRule, new ExactRandomQueryPhase(numberOfInitialRandomQueries),
				new ExactMLQueryPhase(mlComponent, maxQueries, marginalQueriesPerRound), seed);
	}

	public MLCAuction(Domain domain, OutcomeRuleGenerator outcomeRule,
			RandomQueryPhase<BundleExactValueBids> initialPhase, MLQueryPhase<BundleExactValueBids> mlPhase,
			Long seed) {
		super(domain, outcomeRule, initialPhase, seed);
		this.addAuctionPhase(mlPhase);
		this.setMaxRounds(1000);
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
}
