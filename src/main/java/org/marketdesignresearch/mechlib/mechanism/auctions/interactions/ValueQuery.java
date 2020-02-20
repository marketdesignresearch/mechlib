package org.marketdesignresearch.mechlib.mechanism.auctions.interactions;

import java.util.Set;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;

public interface ValueQuery<T extends BundleValuePair> extends Interaction<BundleValueBid<T>> {
	Set<Bundle> getQueriedBundles();
}
