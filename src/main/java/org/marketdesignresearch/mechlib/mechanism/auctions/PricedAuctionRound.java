package org.marketdesignresearch.mechlib.mechanism.auctions;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.core.price.Prices;

/**
 * An AuctionRound involving prices should implement this interface.
 * 
 * @author Manuel Beyeler
 *
 * @param <BB> the bid type of this AuctionRound
 */
public interface PricedAuctionRound<BB extends BundleValueBids<?>> extends AuctionRound<BB> {

	Prices getPrices();
}
