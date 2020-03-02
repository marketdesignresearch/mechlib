package org.marketdesignresearch.mechlib.mechanism.auctions.interactions;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;

/**
 * Also check activity rules and validity here for submitted bids
 * 
 * @author Manuel Beyeler
 *
 * @param <T>
 */
public interface Interaction<B extends BundleValuePair> {
	Bidder getBidder();
	void setAuction(Auction<B> auction);
	void submitProposedBid();
	Class<? extends Interaction<B>> getType();
}
