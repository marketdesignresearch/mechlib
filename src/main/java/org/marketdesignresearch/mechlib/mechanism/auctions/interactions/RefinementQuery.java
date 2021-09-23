package org.marketdesignresearch.mechlib.mechanism.auctions.interactions;

import java.util.Set;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBid;
import org.marketdesignresearch.mechlib.core.price.Prices;

/**
 * Bidders are asked to refine their active bids and submit new (possible
 * update) bids for all bundles in {@link #getLatestActiveBid()} such that they
 * comply with the specified activity rules ({@link #getRefinementTypes()}).
 * 
 * @author Manuel Beyeler
 */
public interface RefinementQuery extends TypedInteraction<BundleBoundValueBid> {

	/**
	 * @return the (refinement) activity rules that must be considered
	 */
	public Set<RefinementType> getRefinementTypes();

	/**
	 * @return the provisional allocation
	 */
	public Bundle getProvisionalAllocation();

	/**
	 * @return current prices
	 */
	public Prices getPrices();

	/**
	 * @return latest active bids
	 */
	public BundleBoundValueBid getLatestActiveBid();
}
