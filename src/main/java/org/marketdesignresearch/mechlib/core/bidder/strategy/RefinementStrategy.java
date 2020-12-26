package org.marketdesignresearch.mechlib.core.bidder.strategy;

import java.math.BigDecimal;
import java.util.Set;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBid;
import org.marketdesignresearch.mechlib.core.bidder.random.BidderRandom;
import org.marketdesignresearch.mechlib.core.bidder.strategy.impl.AutomatedRefiner;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.ValueFunction;
import org.marketdesignresearch.mechlib.instrumentation.RefinementInstrumentation;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.RefinementQuery;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.RefinementType;

public interface RefinementStrategy extends InteractionStrategy {
	
	 BundleBoundValueBid applyRefinementStrategy(RefinementQuery query, Auction<?> auction);

	default Set<Class<? extends InteractionStrategy>> getTypes() {
		return Set.of(RefinementStrategy.class);
	}	
}
