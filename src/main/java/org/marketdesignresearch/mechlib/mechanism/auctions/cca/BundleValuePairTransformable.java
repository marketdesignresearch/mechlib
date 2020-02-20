package org.marketdesignresearch.mechlib.mechanism.auctions.cca;

import java.util.UUID;

import org.marketdesignresearch.mechlib.core.bid.Bid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.DefaultInteraction;

public abstract class BundleValuePairTransformable<B extends Bid,E extends BundleValuePair> extends DefaultInteraction<B> {
	
	public BundleValuePairTransformable(UUID bidder) {
		super(bidder);
	}
	
	public abstract BundleValueBid<E> getBundleValueTransformedBid();
}
