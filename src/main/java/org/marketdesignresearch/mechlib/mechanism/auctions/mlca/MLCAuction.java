package org.marketdesignresearch.mechlib.mechanism.auctions.mlca;

import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.mechanism.auctions.ExactValueAuction;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases.MLQueryPhase;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases.RandomQueryPhase;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRuleGenerator;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class MLCAuction extends ExactValueAuction {

	private static final int DEFAULT_MAX_QUERIES = 100;

	@Setter @Getter
	private int maxQueries = DEFAULT_MAX_QUERIES;
	
	public MLCAuction(Domain domain, OutcomeRuleGenerator outcomeRule, RandomQueryPhase<BundleExactValueBids> initialPhase, MLQueryPhase<BundleExactValueBids> mlPhase) {
		super(domain,outcomeRule,initialPhase);
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
