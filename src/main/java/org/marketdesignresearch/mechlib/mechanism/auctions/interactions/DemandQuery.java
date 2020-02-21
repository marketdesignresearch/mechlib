package org.marketdesignresearch.mechlib.mechanism.auctions.interactions;

import org.marketdesignresearch.mechlib.core.bid.demand.DemandBid;
import org.marketdesignresearch.mechlib.core.price.Prices;

public interface DemandQuery extends Interaction{
	Prices getPrices();
	
	DemandBid proposeDemandBid();
	void submitDemandBid(DemandBid bid);
	
	@Override
	default Class<DemandQuery> getType() {
		return DemandQuery.class;
	}
}
