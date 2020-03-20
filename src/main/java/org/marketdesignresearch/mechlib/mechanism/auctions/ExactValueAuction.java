package org.marketdesignresearch.mechlib.mechanism.auctions;

import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRuleGenerator;
import org.springframework.data.annotation.PersistenceConstructor;

public class ExactValueAuction extends Auction<BundleExactValueBids>{

	public ExactValueAuction(Domain domain, OutcomeRuleGenerator outcomeRuleGenerator,
			AuctionPhase<BundleExactValueBids> firstPhase) {
		super(domain, outcomeRuleGenerator, firstPhase);
	}
	
	@PersistenceConstructor
	protected ExactValueAuction(Domain domain, OutcomeRuleGenerator outcomeRuleGenerator, AuctionRoundBuilder<BundleExactValueBids> current) {
		super(domain,outcomeRuleGenerator,current);
	}

	@Override
	protected BundleExactValueBids join(BundleExactValueBids b1, BundleExactValueBids b2) {
		return b1.join(b2);
	}

	@Override
	protected BundleExactValueBids createEmptyBids() {
		return new BundleExactValueBids();
	}

}
