package org.marketdesignresearch.mechlib.core.bidder.newstrategy;

import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.bidder.newstrategy.truthful.TruthfulDemandQueryStrategy;
import org.marketdesignresearch.mechlib.core.bidder.newstrategy.truthful.TruthfulExactValueQueryStrategy;

public interface InteractionStrategy {
	
	@SuppressWarnings("unchecked")
	public static <T extends InteractionStrategy> T defaultStrategy(Class<T> type, Bidder bidder) {
		if(type.isAssignableFrom(DemandQueryStrategy.class)) {
			return (T) new TruthfulDemandQueryStrategy(bidder);
		} 
		if(type.isAssignableFrom(ExactValueQueryStrategy.class)) {
			return (T) new TruthfulExactValueQueryStrategy(bidder);
		}
		throw new IllegalArgumentException("Unknown Strategy");
	}
}
