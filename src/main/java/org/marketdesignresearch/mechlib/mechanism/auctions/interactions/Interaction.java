package org.marketdesignresearch.mechlib.mechanism.auctions.interactions;

import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;

/**
 * Also check activity rules and validity here for submitted bids
 * 
 * TODO documentation
 * 
 * @author Manuel Beyeler
 *
 * @param <T>
 */
public interface Interaction {
	Bidder getBidder();
	void setAuction(Auction<?> auction);
	void submitProposedBid();
	Class<? extends Interaction> getType();
}
