package org.marketdesignresearch.mechlib.mechanism.auctions.interactions;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBoundPair;

public interface BoundValueQuery extends ValueQuery<BundleValueBoundPair>{

	@Override
	default Class<BoundValueQuery> getType() {
		return BoundValueQuery.class;
	}
}
