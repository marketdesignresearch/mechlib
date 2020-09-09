package org.marketdesignresearch.mechlib.core.bidder.valuefunction.transform;

import java.util.Set;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBid;
import org.marketdesignresearch.mechlib.core.bidder.strategy.InteractionStrategy;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.SimpleBidInteraction;

public interface SimpleBidTransformation extends InteractionStrategy {
	BundleExactValueBid applySimpleBidStrategy(SimpleBidInteraction interaction, Auction<?> auction);

	default Set<Class<? extends InteractionStrategy>> getTypes() {
		return Set.of(SimpleBidTransformation.class);
	}
}
