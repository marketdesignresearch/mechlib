package org.marketdesignresearch.mechlib.core.bidder.newstrategy;

import java.util.HashMap;
import java.util.Map;

import org.marketdesignresearch.mechlib.core.bidder.newstrategy.truthful.TruthfulDemandQueryStrategy;
import org.marketdesignresearch.mechlib.core.bidder.newstrategy.truthful.TruthfulExactValueQueryStrategy;
import org.marketdesignresearch.mechlib.core.bidder.newstrategy.truthful.TruthfulProfitMaxQueryStrategy;

public class DefaultStrategyHandler {

	private DefaultStrategyHandler() {}
	
	private static Map<Class<? extends InteractionStrategy>, Class<? extends InteractionStrategy>> defaultStrategies = new HashMap<>();
	
	static {
		defaultStrategies.put(DemandQueryStrategy.class, TruthfulDemandQueryStrategy.class);
		defaultStrategies.put(ExactValueQueryStrategy.class, TruthfulExactValueQueryStrategy.class);
		defaultStrategies.put(ProfitMaxStrategy.class, TruthfulProfitMaxQueryStrategy.class);
	}
	
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
