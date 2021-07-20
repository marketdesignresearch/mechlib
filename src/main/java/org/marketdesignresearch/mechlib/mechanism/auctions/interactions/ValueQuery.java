package org.marketdesignresearch.mechlib.mechanism.auctions.interactions;

import java.util.Set;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;

/**
 * As an aswer to a value query a bidder must submit the value for all queried
 * bundles
 * 
 * @author Manuel Beyeler
 *
 * @param <B> the BundleValueBid type (i.e. exact or bounds)
 */
public interface ValueQuery<B extends BundleValueBid<?>> extends TypedInteraction<B> {
	/**
	 * @return the queried bundles
	 */
	Set<Bundle> getQueriedBundles();
}
