package org.marketdesignresearch.mechlib.core.bidder.newstrategy;

import java.util.Set;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBid;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.BoundValueQuery;

public interface BoundValueQueryStrategy extends InteractionStrategy {
	BundleBoundValueBid applyBoundValueStrategy(BoundValueQuery interaction);
	
	default Set<Class<? extends InteractionStrategy>> getTypes() {
		return Set.of(BoundValueQueryStrategy.class);
	}
}
