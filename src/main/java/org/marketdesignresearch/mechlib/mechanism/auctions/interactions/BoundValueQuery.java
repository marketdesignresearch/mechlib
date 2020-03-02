package org.marketdesignresearch.mechlib.mechanism.auctions.interactions;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBoundPair;

public interface BoundValueQuery extends ValueQuery<BundleValueBoundPair>{

	BundleValueBid<BundleValueBoundPair> proposeBid();
	void submitBids(BundleValueBid<BundleValueBoundPair> bid);
	BundleValueBid<BundleValueBoundPair> getBid();
	
	@Override
	default Class<BoundValueQuery> getType() {
		return BoundValueQuery.class;
	}
}
