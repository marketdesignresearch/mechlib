package org.marketdesignresearch.mechlib.core.bidder.newstrategy.truthful;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.WeakHashMap;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValuePair;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BoundRandomHelper {
	
	public static BigDecimal DEFAULT_STD_FOR_BOUNDS_GENERATION = BigDecimal.valueOf(0.5);
	
	// To not prevent garbage collection of a model if it is not used anymore
	private static Map<Bidder,Random> randomMap = new WeakHashMap<>();
	private static Map<Bidder,BigDecimal> stdMap = new WeakHashMap<>();
	
	public static void initDomain(Domain domain, long seed) {
		initDomain(domain,new Random(seed), DEFAULT_STD_FOR_BOUNDS_GENERATION);
	}
	
	public static void initDomain(Domain domain, Random random, BigDecimal stdDeviation) {
		domain.getBidders().forEach(b -> randomMap.put(b, new Random(random.nextLong())));
		domain.getBidders().forEach(b -> stdMap.put(b, stdDeviation));
	}
	
	public static Random getRandom(Bidder bidder) {
		if(!randomMap.containsKey(bidder) ) {
			log.warn("Random object for bidder {} not initialized. "
					+ "For a reproducable result make sure to call BoundRandomHelper#initDomain "
					+ "before using an auction with strategies using bounds"
					,bidder);
			randomMap.put(bidder, new Random());
			stdMap.put(bidder, DEFAULT_STD_FOR_BOUNDS_GENERATION);
		}
		return randomMap.get(bidder);
	}
	
	/**
	 * Generates a random number using the central limit theorem
	 * @return
	 */
	public static double getNextGuassianLikeDouble(Bidder b) {
		Random random = getRandom(b);
		double value = 0;
		int j = 2;
		for(int i =0; i<j; i++) {
			value += random.nextDouble();
		}
		return value / j;
	}
	
	/**
	 * Generate random truthful bounds
	 * @param bundle
	 * @return
	 */
	public static BundleBoundValuePair getValueBoundsForBundle(Bidder bidder, Bundle bundle) {

		Random random = getRandom(bidder);
		
		BigDecimal value = bidder.getValue(bundle);
		BigDecimal lowerBound = value.subtract(BigDecimal.valueOf(random.nextGaussian())
														 .abs()
														 .multiply(stdMap.get(bidder)
														 .multiply(value)))
						              .max(BigDecimal.ZERO);
		BigDecimal upperBound = value.add(BigDecimal.valueOf(random.nextGaussian())
													.abs()
													.multiply(stdMap.get(bidder)
													.multiply(value))
										);
		return new BundleBoundValuePair(lowerBound, upperBound, bundle, UUID.randomUUID().toString());
	}
	
}
