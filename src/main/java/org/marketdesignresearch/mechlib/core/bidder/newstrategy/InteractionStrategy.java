package org.marketdesignresearch.mechlib.core.bidder.newstrategy;

import org.marketdesignresearch.mechlib.core.bid.Bid;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.DemandQuery;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.Interaction;

public interface InteractionStrategy<B extends Bid, T extends Interaction<B>> {
	B applyStrategy(T interaction);
	
	@SuppressWarnings("unchecked")
	public static <B extends Bid, T extends Interaction<B>> InteractionStrategy<B,T> defaultStrategy(Class<T> type, Bidder bidder) {
		
		if(DemandQuery.class.equals(type)) {
			return (InteractionStrategy<B, T>) new DefaultDemandQueryStrategy(bidder);
		}
		throw new IllegalArgumentException("Unknown Strategy");
	}
}
