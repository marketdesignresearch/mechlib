package org.marketdesignresearch.mechlib.core.bidder.newstrategy;

import java.util.Set;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBid;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.RefinementQuery;

public interface RefinementStrategy extends InteractionStrategy {
	BundleBoundValueBid applyRefinementStrategy(RefinementQuery query);
	
	default Set<Class<? extends InteractionStrategy>> getTypes() {
		return Set.of(RefinementStrategy.class);
	}
}
