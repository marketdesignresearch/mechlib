package org.marketdesignresearch.mechlib.core.bidder.strategy;

import java.util.Set;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBid;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.ConvergenceInteraction;

public interface ConvergenceStrategy extends InteractionStrategy{

	BundleBoundValueBid applyConvergenceStrategy(ConvergenceInteraction query, Auction<?> auction);

	default Set<Class<? extends InteractionStrategy>> getTypes() {
		return Set.of(ConvergenceStrategy.class);
	}
}
