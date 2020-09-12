package org.marketdesignresearch.mechlib.mechanism.auctions.simple;

import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRoundBuilder;
import org.marketdesignresearch.mechlib.mechanism.auctions.ExactValueAuction;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRuleGenerator;
import org.springframework.data.annotation.PersistenceConstructor;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Simple example of an auction where each bidder just submits one bid.
 * 
 * @author Manuel Beyeler
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SimpleBidAuction extends ExactValueAuction {

	public SimpleBidAuction(Domain domain, OutcomeRuleGenerator outcomeRuleGenerator) {
		super(domain, outcomeRuleGenerator, new SimpleBidPhase(), null);
	}

	public SimpleBidAuction(Domain domain, OutcomeRuleGenerator outcomeRuleGenerator, Long seed) {
		super(domain, outcomeRuleGenerator, new SimpleBidPhase(), seed);
	}

	@PersistenceConstructor
	protected SimpleBidAuction(Domain domain, OutcomeRuleGenerator outcomeRuleGenerator,
			AuctionRoundBuilder<BundleExactValueBids> current) {
		super(domain, outcomeRuleGenerator, current);
	}
}
