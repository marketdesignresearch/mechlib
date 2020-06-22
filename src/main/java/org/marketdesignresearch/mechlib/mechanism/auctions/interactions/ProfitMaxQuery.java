package org.marketdesignresearch.mechlib.mechanism.auctions.interactions;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBid;
import org.marketdesignresearch.mechlib.core.price.Prices;

public interface ProfitMaxQuery extends TypedInteraction<BundleExactValueBid>{
	Prices getPrices();
	int getNumberOfBids();
	
	@Override
	default Class<ProfitMaxQuery> getType() {
		return ProfitMaxQuery.class;
	}
}
