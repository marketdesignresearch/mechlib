package org.marketdesignresearch.mechlib.mechanism.auctions.interactions;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBid;

public interface BoundValueQuery extends ValueQuery<BundleBoundValueBid>{
	
	@Override
	default Class<BoundValueQuery> getType() {
		return BoundValueQuery.class;
	}
}
