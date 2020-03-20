package org.marketdesignresearch.mechlib.mechanism.auctions.simple;

import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRoundBuilder;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRuleGenerator;
import org.springframework.data.annotation.PersistenceConstructor;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SimpleBidAuction extends Auction<BundleExactValueBids>{

	public SimpleBidAuction(Domain domain, OutcomeRuleGenerator outcomeRuleGenerator) {
		super(domain, outcomeRuleGenerator, new SimpleBidPhase());
	}
	
	@PersistenceConstructor
	protected SimpleBidAuction(Domain domain, OutcomeRuleGenerator outcomeRuleGenerator, AuctionRoundBuilder<BundleExactValueBids> current) {
		super(domain,outcomeRuleGenerator,current);
	}

	@Override
	protected BundleExactValueBids createEmptyBids() {
		return new BundleExactValueBids();
	}

}
