package org.marketdesignresearch.mechlib.mechanism.auctions.interactions;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBoundPair;

public interface BoundValueQuery extends ValueQuery{

	BundleValueBid<BundleValueBoundPair> proposeBids();
	void submitBids(BundleValueBid<BundleValueBoundPair> bid);
	
	@Override
	default Class<BoundValueQuery> getType() {
		return BoundValueQuery.class;
	}
}
