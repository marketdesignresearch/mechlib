package org.marketdesignresearch.mechlib.mechanism.auctions.simple;

import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRoundBuilder;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRuleGenerator;
import org.springframework.data.annotation.PersistenceConstructor;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SimpleBidAuction extends Auction<BundleValuePair>{

	public SimpleBidAuction(Domain domain, OutcomeRuleGenerator outcomeRuleGenerator) {
		super(domain, outcomeRuleGenerator, new SimpleBidPhase());
	}
	
	@PersistenceConstructor
	protected SimpleBidAuction(Domain domain, OutcomeRuleGenerator outcomeRuleGenerator, AuctionRoundBuilder<BundleValuePair> current) {
		super(domain,outcomeRuleGenerator,current);
	}

}
