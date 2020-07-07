package org.marketdesignresearch.mechlib.mechanism.auctions.interactions;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBid;

public interface ExactValueQuery extends ValueQuery<BundleExactValueBid> {

	@Override
	default Class<ExactValueQuery> getType() {
		return ExactValueQuery.class;
	}
}
