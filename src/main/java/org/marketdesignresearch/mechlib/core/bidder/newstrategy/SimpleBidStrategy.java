package org.marketdesignresearch.mechlib.core.bidder.newstrategy;

import java.util.Set;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBid;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.SimpleBidInteraction;

public interface SimpleBidStrategy extends InteractionStrategy {
	BundleExactValueBid applySimpleBidStrategy(SimpleBidInteraction interaction);
	
	default Set<Class<? extends InteractionStrategy>> getTypes() {
		return Set.of(SimpleBidStrategy.class);
	}
}
