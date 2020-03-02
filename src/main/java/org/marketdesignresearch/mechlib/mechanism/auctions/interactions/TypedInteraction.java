package org.marketdesignresearch.mechlib.mechanism.auctions.interactions;

import org.marketdesignresearch.mechlib.core.bid.Bid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;

public interface TypedInteraction<B extends Bid, BV extends BundleValuePair> extends Interaction<BV> {
	void submitBid(B bid);
	B proposeBid();
	B getBid();
}
