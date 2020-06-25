package org.marketdesignresearch.mechlib.mechanism.auctions.simple;

import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRoundBuilder;
import org.marketdesignresearch.mechlib.mechanism.auctions.ExactValueAuction;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRuleGenerator;
import org.springframework.data.annotation.PersistenceConstructor;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SimpleBidAuction extends ExactValueAuction{

	public SimpleBidAuction(Domain domain, OutcomeRuleGenerator outcomeRuleGenerator) {
		this(domain,outcomeRuleGenerator,null);
	}
	
	public SimpleBidAuction(Domain domain, OutcomeRuleGenerator outcomeRuleGenerator, Long seed) {
		super(domain, outcomeRuleGenerator, new SimpleBidPhase(), seed);
	}
	
	@PersistenceConstructor
	protected SimpleBidAuction(Domain domain, OutcomeRuleGenerator outcomeRuleGenerator, AuctionRoundBuilder<BundleExactValueBids> current, Long seed) {
		super(domain,outcomeRuleGenerator,current, seed);
	}
}
