package org.marketdesignresearch.mechlib.mechanism.auctions.interactions;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;
import org.marketdesignresearch.mechlib.core.bid.demand.DemandBid;
import org.marketdesignresearch.mechlib.core.price.Prices;

public interface DemandQuery extends TypedInteraction<DemandBid, BundleValuePair>{
	Prices getPrices();
	
	@Override
	default Class<DemandQuery> getType() {
		return DemandQuery.class;
	}
}
