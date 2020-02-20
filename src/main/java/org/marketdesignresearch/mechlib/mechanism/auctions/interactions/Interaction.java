package org.marketdesignresearch.mechlib.mechanism.auctions.interactions;

import org.marketdesignresearch.mechlib.core.bid.Bid;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;

/**
 * Also check activity rules here for submitted bids
 * 
 * @author Manuel Beyeler
 *
 * @param <T>
 */
public interface Interaction<T extends Bid> {
	T proposeBid();
	void submitBid(T bid);
	Bidder getBidder();
	
	Class<? extends Interaction<T>> getType();
}
