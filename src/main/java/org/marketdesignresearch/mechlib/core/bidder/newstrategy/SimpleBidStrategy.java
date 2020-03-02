package org.marketdesignresearch.mechlib.core.bidder.newstrategy;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.SimpleBidInteraction;

public interface SimpleBidStrategy extends InteractionStrategy {
	BundleValueBid<BundleValuePair> applySimpleBidStrategy(SimpleBidInteraction interaction);
}
