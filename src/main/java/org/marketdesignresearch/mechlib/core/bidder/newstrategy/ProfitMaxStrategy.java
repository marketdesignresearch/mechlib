package org.marketdesignresearch.mechlib.core.bidder.newstrategy;

import java.util.Set;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBid;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.ProfitMaxQuery;

public interface ProfitMaxStrategy extends InteractionStrategy{
	BundleExactValueBid applyProfitMaxStrategy(ProfitMaxQuery interaction);
	
	default Set<Class<? extends InteractionStrategy>> getTypes() {
		return Set.of(ProfitMaxStrategy.class);
	}
}
