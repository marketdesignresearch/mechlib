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


@Slf4j
public class MLCAuction extends ExactValueAuction {
	
	public MLCAuction(Domain domain, OutcomeRuleGenerator outcomeRule, int numberOfInitialRandomQueries, int maxQueries, int marginalQueriesPerRound, SupportVectorSetup svrSetup, Long seed) {
		this(domain, outcomeRule, numberOfInitialRandomQueries, maxQueries, marginalQueriesPerRound, new ExactDistributedSVR(svrSetup),seed);
	}
	
	public MLCAuction(Domain domain, OutcomeRuleGenerator outcomeRule, int numberOfInitialRandomQueries, int maxQueries, int marginalQueriesPerRound, MachineLearningComponent<BundleExactValueBids> mlComponent, Long seed) {
		this(domain, outcomeRule, new ExactRandomQueryPhase(numberOfInitialRandomQueries), new ExactMLQueryPhase(mlComponent, maxQueries, marginalQueriesPerRound),seed);
	}
	
	public MLCAuction(Domain domain, OutcomeRuleGenerator outcomeRule, RandomQueryPhase<BundleExactValueBids> initialPhase, MLQueryPhase<BundleExactValueBids> mlPhase, Long seed) {
		super(domain,outcomeRule,initialPhase,seed);
		this.addAuctionPhase(mlPhase);
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
}
