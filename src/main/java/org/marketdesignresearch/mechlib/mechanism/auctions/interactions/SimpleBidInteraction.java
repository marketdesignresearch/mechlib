package org.marketdesignresearch.mechlib.mechanism.auctions.interactions;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBid;

public interface SimpleBidInteraction extends TypedInteraction<BundleExactValueBid> {
	@Override
	default Class<? extends Interaction> getType() {
		return SimpleBidInteraction.class;
	}
}
