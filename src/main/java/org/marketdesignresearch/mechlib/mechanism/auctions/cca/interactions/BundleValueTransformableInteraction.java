package org.marketdesignresearch.mechlib.mechanism.auctions.cca.interactions;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.Interaction;

public interface BundleValueTransformableInteraction<T extends BundleValuePair> extends Interaction{
	BundleValueBid<T> getTransformedBid();
	void setAuction(Auction<T> auction);
}
