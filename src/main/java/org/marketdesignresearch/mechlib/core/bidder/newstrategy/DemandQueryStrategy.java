package org.marketdesignresearch.mechlib.core.bidder.newstrategy;

import java.util.Set;

import org.marketdesignresearch.mechlib.core.bid.demand.DemandBid;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.DemandQuery;

public interface DemandQueryStrategy extends InteractionStrategy {
	DemandBid applyDemandStrategy(DemandQuery query, Auction<?> auction);
	
	default Set<Class<? extends InteractionStrategy>> getTypes() {
		return Set.of(DemandQueryStrategy.class);
	}
}
