package org.marketdesignresearch.mechlib.mechanism.auctions.interactions;

import org.marketdesignresearch.mechlib.core.bid.Bid;

public interface TypedInteraction<B extends Bid> extends Interaction {
	void submitBid(B bid);
	B proposeBid();
	B getBid();
}
