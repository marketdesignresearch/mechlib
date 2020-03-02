package org.marketdesignresearch.mechlib.mechanism.auctions.interactions;

import java.util.Set;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;

public interface ValueQuery<B extends BundleValuePair> extends TypedInteraction<BundleValueBid<B>,B> {
	Set<Bundle> getQueriedBundles();
}
