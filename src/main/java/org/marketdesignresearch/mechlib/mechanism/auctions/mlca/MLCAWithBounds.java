package org.marketdesignresearch.mechlib.mechanism.auctions.mlca;

import java.math.BigDecimal;
import java.util.Set;

import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases.MLQueryPhase;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases.RandomQueryPhase;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases.RefinementPhase;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRuleGenerator;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MLCAWithBounds extends Auction<BundleBoundValueBids>{

	private static final int DEFAULT_MAX_QUERIES = 100;

	@Setter @Getter
	private int maxQueries = DEFAULT_MAX_QUERIES;
	
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
