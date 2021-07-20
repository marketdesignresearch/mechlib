package org.marketdesignresearch.mechlib.core.bidder.strategy.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBid;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.ValueFunction;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.DIARRefinement;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.MRPARRefinement;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.RefinementType;

/**
 * Interface for automated refiners for one activity rule
 * 
 * @author Manuel Beyeler
 *
 * @param <E>
 */
public abstract class AutomatedRefiner<E extends RefinementType> {
	/**
	 * @param type                  refinement type
	 * @param v                     the valuefunction
	 * @param activeBids            bids active at the beginning of this refinement
	 *                              round
	 * @param refinedBids           bids that might have been refined by a previous
	 *                              called refiner (otherwise equals activeBids)
	 * @param prices                prices
	 * @param provisionalAllocation provisional allocation of given bidder
	 * @param random                Random instance
	 * @return refined bids
	 */
	public abstract BundleBoundValueBid refineBids(E type, ValueFunction v, BundleBoundValueBid activeBids,
			BundleBoundValueBid refinedBids, Prices prices, Bundle provisionalAllocation, Random random);

	/**
	 * Generates a random number using the central limit theorem
	 * 
	 * @return
	 */
	protected double getNextGuassianLikeDouble(Random random) {
		double value = 0;
		int j = 2;
		for (int i = 0; i < j; i++) {
			value += random.nextDouble();
		}
		return value / j;
	}

	@SuppressWarnings("rawtypes")
	private static Map<Class<? extends RefinementType>, AutomatedRefiner> refiners;

	static {
		refiners = new HashMap<>();
		refiners.put(DIARRefinement.class, new DIARRefiner());
		refiners.put(MRPARRefinement.class, new MRPARRefiner());
	}

	@SuppressWarnings("unchecked")
	public static <E extends RefinementType> AutomatedRefiner<E> getRefiner(E type) {
		return refiners.get(type.getClass());
	}

	public static BundleBoundValueBid refine(RefinementType type, ValueFunction valueFunction,
			BundleBoundValueBid activeBids, BundleBoundValueBid refinedBids, Prices prices,
			Bundle provisionalAllocation, Random random) {
		return getRefiner(type).refineBids(type, valueFunction, activeBids, refinedBids, prices, provisionalAllocation,
				random);
	}
}
