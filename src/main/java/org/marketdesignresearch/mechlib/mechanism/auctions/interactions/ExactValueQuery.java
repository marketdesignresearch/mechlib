package org.marketdesignresearch.mechlib.mechanism.auctions.interactions;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;

public interface ExactValueQuery extends ValueQuery {

	BundleValueBid<BundleValuePair> proposeExactValueBids();
	void submitExactValueBids(BundleValueBid<BundleValuePair> bid);
	
	@Override
	default Class<ExactValueQuery> getType() {
		return ExactValueQuery.class;
	}
}
