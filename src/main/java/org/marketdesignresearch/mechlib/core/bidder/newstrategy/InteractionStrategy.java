package org.marketdesignresearch.mechlib.core.bidder.newstrategy;

import java.util.Set;

import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.bidder.newstrategy.truthful.TruthfulDemandQueryStrategy;
import org.marketdesignresearch.mechlib.core.bidder.newstrategy.truthful.TruthfulExactValueQueryStrategy;
import org.marketdesignresearch.mechlib.core.bidder.newstrategy.truthful.TruthfulProfitMaxQueryStrategy;

/**
 * A strategy defines how a bidder will respond to an auction interaction.
 * 
 * Not that a strategy is bidder specific and each bidder must hold her own strategy.
 * So strategy objects might not be shared among bidders.
 * 
 * @author Manuel Beyeler
 */
public interface InteractionStrategy {
	
	/**
	 * @return all strategy types that are implemented by this class
	 */
	Set<Class<? extends InteractionStrategy>> getTypes();
	
	/**
	 * This method will be called by the bidder when a strategy is added to a bidder.
	 * This method should be also called when a bidder is deserialized such that a 
	 * strategy can hold a transient reference to the bidder.
	 * 
	 * @param bidder the bidder to which this strategy belongs
	 */
	void setBidder(Bidder bidder);
	
	@SuppressWarnings("unchecked")
	public static <T extends InteractionStrategy> T defaultStrategy(Class<T> type) {
		if(type.isAssignableFrom(DemandQueryStrategy.class)) {
			return (T) new TruthfulDemandQueryStrategy();
		} 
		if(type.isAssignableFrom(ExactValueQueryStrategy.class)) {
			return (T) new TruthfulExactValueQueryStrategy();
		}
		if(type.isAssignableFrom(ProfitMaxStrategy.class)) {
			return (T) new TruthfulProfitMaxQueryStrategy();
		}
		throw new IllegalArgumentException("Unknown Strategy");
	}
}
