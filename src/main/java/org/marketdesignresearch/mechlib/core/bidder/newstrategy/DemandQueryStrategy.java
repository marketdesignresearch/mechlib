package org.marketdesignresearch.mechlib.core.bidder.newstrategy;

import org.marketdesignresearch.mechlib.core.bid.demand.DemandBid;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.DemandQuery;

public interface DemandQueryStrategy extends InteractionStrategy {
	DemandBid applyDemandStrategy(DemandQuery query);
}
