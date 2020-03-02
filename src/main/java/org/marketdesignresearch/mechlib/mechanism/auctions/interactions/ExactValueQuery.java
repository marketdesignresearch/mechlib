package org.marketdesignresearch.mechlib.mechanism.auctions.interactions;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;

public interface ExactValueQuery extends ValueQuery<BundleValuePair> {
	
	@Override
	default Class<ExactValueQuery> getType() {
		return ExactValueQuery.class;
	}
}
