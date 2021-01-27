package org.marketdesignresearch.mechlib.core.bidder.strategy.impl;

import java.math.BigDecimal;
import java.util.Random;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValuePair;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.ValueFunction;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.DIARVariant3Refinement;

public class DIARVariant3Refiner extends AutomatedRefiner<DIARVariant3Refinement>{

	public static BigDecimal roundingDelta = BigDecimal.valueOf(1E-8d);
	
	@Override
	public BundleBoundValueBid refineBids(DIARVariant3Refinement type, ValueFunction b, BundleBoundValueBid activeBids,
			BundleBoundValueBid refinedBids, Prices prices, Bundle provisionalAllocation, Random random) {
		
		BundleBoundValueBid returnBid = refinedBids.copy();
		
		for(Bundle bundle : type.getBundles()) {
			
			BundleBoundValuePair activeBid = activeBids.getBidForBundle(bundle);
			BundleBoundValuePair refinedBid = refinedBids.getBidForBundle(bundle);
			BigDecimal trueValue = b.getValue(bundle);
			
			if(refinedBid.getUpperBound().subtract(refinedBid.getLowerBound()).compareTo(type.getEpsilon().add(roundingDelta)) >= 0) {
				// show that reduction is not possible
				BigDecimal lowerDifference = trueValue.subtract(refinedBid.getLowerBound());
				BigDecimal upperDifference = refinedBid.getUpperBound().subtract(trueValue);
				if(lowerDifference.compareTo(upperDifference) > 0) {
					BigDecimal newUpperBound = refinedBid.getUpperBound();
					BigDecimal newLowerBound = refinedBid.getUpperBound().subtract(type.getEpsilon()).add(roundingDelta);
					if(newLowerBound.compareTo(trueValue) > 0) {
						newLowerBound = trueValue;
						newUpperBound = newLowerBound.add(type.getEpsilon()).subtract(roundingDelta);
					}
					newUpperBound = newUpperBound.min(refinedBid.getUpperBound()).max(trueValue);
					newLowerBound = newLowerBound.max(refinedBid.getLowerBound()).min(trueValue);
					
					returnBid.addBundleBid(new BundleBoundValuePair(newLowerBound,newUpperBound,refinedBid.getBundle(),refinedBid.getId()));
				} else {
					BigDecimal newLowerBound = refinedBid.getLowerBound();
					BigDecimal newUpperBound = refinedBid.getLowerBound().add(type.getEpsilon()).subtract(roundingDelta);
					if(newUpperBound.compareTo(trueValue) < 0) {
						newUpperBound = trueValue;
						newLowerBound = newUpperBound.subtract(type.getEpsilon()).add(roundingDelta);
					}
					newUpperBound = newUpperBound.min(refinedBid.getUpperBound()).max(trueValue);
					newLowerBound = newLowerBound.max(refinedBid.getLowerBound()).min(trueValue);
					
					returnBid.addBundleBid(new BundleBoundValuePair(newLowerBound,newUpperBound,refinedBid.getBundle(),refinedBid.getId()));
				}
			}
		}
		return returnBid;
	}
}
