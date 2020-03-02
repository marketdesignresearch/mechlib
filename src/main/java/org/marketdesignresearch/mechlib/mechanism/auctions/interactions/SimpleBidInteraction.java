package org.marketdesignresearch.mechlib.mechanism.auctions.interactions;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;

public interface SimpleBidInteraction extends TypedInteraction<BundleValueBid<BundleValuePair>, BundleValuePair> {
	@Override
	default Class<? extends Interaction<BundleValuePair>> getType() {
		return SimpleBidInteraction.class;
	}
}
