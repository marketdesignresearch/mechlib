package org.marketdesignresearch.mechlib.mechanism.auctions.interactions;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBid;
import org.marketdesignresearch.mechlib.core.price.Prices;

/**
 * A bidder must report values for all queried bundles ({@link ValueQuery#getQueriedBundles()})
 * as well as for all active bids ({@link BoundValueQueryWithMRPARRefinement#getLatestActiveBid()})
 * while to comply with the modified revealed preference activity rule (MRPAR) for given prices
 * and provisional allocated bundle.
 * 
 * MRPAR by Lubin et. al. (2008).
 * 
 * @author Manuel Beyeler
 */
public interface BoundValueQueryWithMRPARRefinement extends BoundValueQuery {

	/**
	 * @return the provisional allocatedbundle for the MRPAR
	 */
	public Bundle getProvisionalAllocation();

	/**
	 * @return (bundle) prices for the MRPAR
	 */
	public Prices getPrices();

	/**
	 * @return the latest active bids for this bidder that must be updated and included in the response
	 */
	public BundleBoundValueBid getLatestActiveBid();
}
