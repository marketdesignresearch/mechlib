package org.marketdesignresearch.mechlib.mechanism.auctions.interactions;

import org.marketdesignresearch.mechlib.core.bid.demand.DemandBid;
import org.marketdesignresearch.mechlib.core.price.Prices;

/**
 * In a demand query a bidder must specify which bundle is his most prefered
 * bundle for the given prices. A bidder must submit a DemandBid
 * 
 * @author Manuel Beyeler
 * @see DemandBid
 */
public interface DemandQuery extends TypedInteraction<DemandBid> {
	/**
	 * @return the (bundle) prices
	 */
	Prices getPrices();
}
