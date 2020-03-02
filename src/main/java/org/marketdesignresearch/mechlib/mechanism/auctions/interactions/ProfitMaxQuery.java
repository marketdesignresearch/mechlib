package org.marketdesignresearch.mechlib.mechanism.auctions.interactions;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;
import org.marketdesignresearch.mechlib.core.price.Prices;

public interface ProfitMaxQuery extends TypedInteraction<BundleValueBid<BundleValuePair>, BundleValuePair>{
	Prices getPrices();
	int getNumberOfBids();
	
	@Override
	default Class<ProfitMaxQuery> getType() {
		return ProfitMaxQuery.class;
	}
}
