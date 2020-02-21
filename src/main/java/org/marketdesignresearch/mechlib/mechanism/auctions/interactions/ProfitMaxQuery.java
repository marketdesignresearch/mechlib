package org.marketdesignresearch.mechlib.mechanism.auctions.interactions;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;
import org.marketdesignresearch.mechlib.core.price.Prices;

public interface ProfitMaxQuery extends Interaction{
	Prices getPrices();
	int getNumberOfBids();
	
	BundleValueBid<BundleValuePair> proposeExactValueBid();
	void submitExactValueBid(BundleValueBid<BundleValuePair> bid);
	
	@Override
	default Class<ProfitMaxQuery> getType() {
		return ProfitMaxQuery.class;
	}
}
