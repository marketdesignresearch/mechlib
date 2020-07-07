package org.marketdesignresearch.mechlib.mechanism.auctions;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.core.price.Prices;

public interface PricedAuctionRound<BB extends BundleValueBids<?>> extends AuctionRound<BB> {
	
	Prices getPrices();
}
