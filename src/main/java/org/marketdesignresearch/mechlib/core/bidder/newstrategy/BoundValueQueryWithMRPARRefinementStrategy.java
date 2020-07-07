package org.marketdesignresearch.mechlib.core.bidder.newstrategy;

import java.util.Set;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBid;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.BoundValueQueryWithMRPARRefinement;

public interface BoundValueQueryWithMRPARRefinementStrategy extends InteractionStrategy{
	BundleBoundValueBid applyBoundValueQueryWithMRPARRefinementStrategy(BoundValueQueryWithMRPARRefinement query);
	
	default Set<Class<? extends InteractionStrategy>> getTypes() {
		return Set.of(BoundValueQueryWithMRPARRefinementStrategy.class);
	}
}
