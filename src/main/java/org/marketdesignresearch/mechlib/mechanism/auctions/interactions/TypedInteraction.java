package org.marketdesignresearch.mechlib.mechanism.auctions.interactions;

import org.marketdesignresearch.mechlib.core.bid.Bid;

/**
 * An interaction providing methods to submit a certain bid type
 * 
 * @author Manuel Beyeler
 * @param <B> the bid type
 * @see Bid
 */
public interface TypedInteraction<B extends Bid> extends Interaction {
	void submitBid(B bid);

	B proposeBid();

	B getBid();
}
