package org.marketdesignresearch.mechlib.core.bidder.newstrategy;

import java.util.HashMap;
import java.util.Map;

import org.marketdesignresearch.mechlib.core.bidder.newstrategy.truthful.TruthfulBoundValueQueryStrategy;
import org.marketdesignresearch.mechlib.core.bidder.newstrategy.truthful.TruthfulDemandQueryStrategy;
import org.marketdesignresearch.mechlib.core.bidder.newstrategy.truthful.TruthfulExactValueQueryStrategy;
import org.marketdesignresearch.mechlib.core.bidder.newstrategy.truthful.TruthfulProfitMaxQueryStrategy;
import org.marketdesignresearch.mechlib.core.bidder.newstrategy.truthful.TruthfulRefinementStrategy;

public class DefaultStrategyHandler {

	private DefaultStrategyHandler() {}
	
	private static Map<Class<? extends InteractionStrategy>, Class<? extends InteractionStrategy>> defaultStrategies = new HashMap<>();
	
	static {
		defaultStrategies.put(DemandQueryStrategy.class, TruthfulDemandQueryStrategy.class);
		defaultStrategies.put(ExactValueQueryStrategy.class, TruthfulExactValueQueryStrategy.class);
		defaultStrategies.put(ProfitMaxStrategy.class, TruthfulProfitMaxQueryStrategy.class);
		defaultStrategies.put(BoundValueQueryWithMRPARRefinementStrategy.class, TruthfulBoundValueQueryStrategy.class);
		defaultStrategies.put(RefinementStrategy.class, TruthfulRefinementStrategy.class);
		defaultStrategies.put(BoundValueQueryStrategy.class, TruthfulBoundValueQueryStrategy.class);
	}
	
	/**
	 * Allows to add a new default strategy handler also for new strategy types which are not part of the library
	 * (I.e. when designing a new auction with some new interaction you can register the default strategy here
	 * instead of setting the strategy for each involved bidder individually)
	 * @param type
	 * @param implementation
	 */
	
	public static void addDefaultHandler(Class<? extends InteractionStrategy> type, Class<? extends InteractionStrategy> implementation) {
		defaultStrategies.put(type, implementation);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends InteractionStrategy> T defaultStrategy(Class<T> type) {
		try {
			return (T)defaultStrategies.get(type).newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
