package org.marketdesignresearch.mechlib.instrumentation;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBid;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.RefinementType;

public interface RefinementInstrumentation {

	public static RefinementInstrumentation NO_OP = new RefinementInstrumentation() {
	};

	default void preRefinement(RefinementType type, Bidder bidder, BundleBoundValueBid activeBids,
			BundleBoundValueBid refinedBids, Prices prices, Bundle provisionalAllocation) {

	}

	default void postRefinement(RefinementType type, Bidder bidder, BundleBoundValueBid activeBids,
			BundleBoundValueBid refinedBids, Prices prices, Bundle provisionalAllocation) {

	}
}
