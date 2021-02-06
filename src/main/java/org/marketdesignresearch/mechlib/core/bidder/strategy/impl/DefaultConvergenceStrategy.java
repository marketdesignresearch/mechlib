package org.marketdesignresearch.mechlib.core.bidder.strategy.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValuePair;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.bidder.random.BidderRandom;
import org.marketdesignresearch.mechlib.core.bidder.strategy.ConvergenceStrategy;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.ValueFunction;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.ConvergenceInteraction;

import lombok.Getter;
import lombok.Setter;

public abstract class DefaultConvergenceStrategy implements ConvergenceStrategy{

	private final static BigDecimal EPSILON_TIGHTENING_FACTOR = BigDecimal.valueOf(0.99);
	
	@Setter
	@Getter
	private transient Bidder bidder;

	@Override
	public BundleBoundValueBid applyConvergenceStrategy(ConvergenceInteraction query, Auction<?> auction) {
		BidderRandom.INSTANCE.getRandom();
		
		BundleBoundValueBid bid = new BundleBoundValueBid();
		
		BigDecimal epsilon = query.getEpsilon().multiply(EPSILON_TIGHTENING_FACTOR);
		for(Bundle bundle : query.getBundles()) {
			BundleBoundValuePair currentPair = query.getLatestActiveBid().getBidForBundle(bundle);
			BigDecimal upperUncertainty = epsilon.multiply(BigDecimal.valueOf(this.getNextGuassianLikeDouble(BidderRandom.INSTANCE.getRandom())));
			upperUncertainty = upperUncertainty.min(epsilon);
			
			BigDecimal upperBound = getValueFunction().getValue(bundle).divide(BigDecimal.ONE.subtract(upperUncertainty), Math.max(10,upperUncertainty.scale()), RoundingMode.DOWN);
			upperBound = upperBound.max(currentPair.getLowerBound().divide(BigDecimal.ONE.subtract(epsilon), Math.max(10,upperUncertainty.scale()), RoundingMode.DOWN));
			upperBound = upperBound.min(currentPair.getUpperBound());
			upperBound = upperBound.max(getValueFunction().getValue(bundle));
			
			BigDecimal lowerBound = upperBound.subtract(upperBound.multiply(epsilon));
			lowerBound = lowerBound.max(currentPair.getLowerBound());
			lowerBound = lowerBound.min(getValueFunction().getValue(bundle));
			
			bid.addBundleBid(new BundleBoundValuePair(lowerBound, upperBound, bundle, currentPair.getId()));
		}
		
		return bid;
	}
	
	public abstract ValueFunction getValueFunction();
	
	private double getNextGuassianLikeDouble(Random random) {
		double value = 0;
		int j = 2;
		for (int i = 0; i < j; i++) {
			value += random.nextDouble();
		}
		return value / j;
	}
}
