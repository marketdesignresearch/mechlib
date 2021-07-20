package org.marketdesignresearch.mechlib.mechanism.auctions.interactions;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBid;
import org.marketdesignresearch.mechlib.core.price.Prices;

/**
 * A bidder might submit up to {@link #getNumberOfBids()} bids and might use the
 * provided prices to generate such bids
 * 
 * @author Manuel Beyeler
 */
public interface ProfitMaxQuery extends TypedInteraction<BundleExactValueBid> {
	/**
	 * @return (bundle) prices
	 */
	Prices getPrices();

	/**
	 * @return maximum number of bids that can be submitted
	 */
	int getNumberOfBids();
}
