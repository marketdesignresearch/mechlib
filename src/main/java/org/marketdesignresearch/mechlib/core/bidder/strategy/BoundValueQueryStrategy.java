package org.marketdesignresearch.mechlib.core.bidder.strategy;

import java.util.Set;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBid;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.BoundValueQuery;

public interface BoundValueQueryStrategy extends InteractionStrategy {
	BundleBoundValueBid applyBoundValueStrategy(BoundValueQuery interaction, Auction<?> auction);

	default Set<Class<? extends InteractionStrategy>> getTypes() {
		return Set.of(BoundValueQueryStrategy.class);
	}
}
