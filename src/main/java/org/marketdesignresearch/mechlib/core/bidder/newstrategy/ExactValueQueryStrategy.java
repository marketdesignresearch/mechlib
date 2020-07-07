package org.marketdesignresearch.mechlib.core.bidder.newstrategy;

import java.util.Set;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBid;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.ExactValueQuery;

public interface ExactValueQueryStrategy extends InteractionStrategy {
	BundleExactValueBid applyExactValueStrategy(ExactValueQuery interaction, Auction<?> auction);

	default Set<Class<? extends InteractionStrategy>> getTypes() {
		return Set.of(ExactValueQueryStrategy.class);
	}
}
