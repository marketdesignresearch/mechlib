package org.marketdesignresearch.mechlib.mechanism.auctions.interactions;

import java.util.Set;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValuePair;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;

/**
 * A value query asks the bidder for his values for a given set of bundles.
 * A bidder must report bids (values) for all queried bundles.
 * 
 * @author Manuel Beyeler
 */
public interface ValueQuery<B extends BundleValueBid<?>> extends TypedInteraction<B> {
	Set<Bundle> getQueriedBundles();
	default Bundle getAlreadyWon() {
		return Bundle.EMPTY;
	}
}
