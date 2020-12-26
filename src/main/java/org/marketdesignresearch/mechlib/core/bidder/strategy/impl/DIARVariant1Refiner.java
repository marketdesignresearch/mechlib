package org.marketdesignresearch.mechlib.core.bidder.strategy.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValuePair;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.ValueFunction;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.DIARVariant1Refinement;

public class DIARVariant1Refiner extends AutomatedRefiner<DIARVariant1Refinement> {

	public static BigDecimal roundingDelta = BigDecimal.valueOf(1E-8d);

	@Override
	public BundleBoundValueBid refineBids(DIARVariant1Refinement type, ValueFunction b, BundleBoundValueBid activeBids,
			BundleBoundValueBid refinedBids, Prices prices, Bundle provisionalAllocation, Random random) {

		BundleBoundValueBid returnBid = refinedBids.copy();

		List<ImmutablePair<Bundle, BigDecimal>> errors = this.getDIARErrorOrder(activeBids, prices,
				provisionalAllocation);

		for (ImmutablePair<Bundle, BigDecimal> error : errors) {
			BundleBoundValuePair activeBid = activeBids.getBidForBundle(error.left);
			BundleBoundValuePair refinedBid = refinedBids.getBidForBundle(error.left);
			BigDecimal trueValue = b.getValue(error.left);
			// check if already reduced
			if (refinedBid.getLowerBound().subtract(activeBid.getLowerBound())
					.compareTo(type.getEpsilon().add(roundingDelta)) >= 0) {
				// done with refinement
				break;
			}

			if (activeBid.getBundle().equals(provisionalAllocation)) {
				// check if the lower bond can be increased by epsilon
				if (activeBid.getLowerBound().add(type.getEpsilon()).add(roundingDelta)
						.compareTo(b.getValue(error.left)) <= 0) {
					BigDecimal newLowerBound = activeBid.getLowerBound().add(type.getEpsilon()).add(roundingDelta);
					newLowerBound = newLowerBound.max(refinedBid.getLowerBound());
					newLowerBound = newLowerBound.min(b.getValue(error.left));
					returnBid.addBundleBid(new BundleBoundValuePair(newLowerBound, refinedBid.getUpperBound(),
							refinedBid.getBundle(), refinedBid.getId()));
					// done with refinement
					break;
				}
			} else {
				if (activeBid.getUpperBound().subtract(type.getEpsilon()).subtract(roundingDelta)
						.compareTo(b.getValue(error.left)) >= 0) {
					BigDecimal newUpperBound = activeBid.getUpperBound().subtract(type.getEpsilon()).subtract(roundingDelta);
					newUpperBound = newUpperBound.min(refinedBid.getUpperBound());
					newUpperBound = newUpperBound.max(b.getValue(error.left));
					returnBid.addBundleBid(new BundleBoundValuePair(refinedBid.getLowerBound(), newUpperBound,
							refinedBid.getBundle(), refinedBid.getId()));
					// done with refinement
					break;
				}
			}

			BigDecimal lowerDifference = trueValue.subtract(refinedBid.getLowerBound());
			BigDecimal upperDifference = refinedBid.getUpperBound().subtract(trueValue);
			if (lowerDifference.compareTo(upperDifference) > 0) {
				BigDecimal newUpperBound = refinedBid.getUpperBound();
				BigDecimal newLowerBound = refinedBid.getUpperBound().subtract(type.getEpsilon()).add(roundingDelta);
				if (newLowerBound.compareTo(trueValue) > 0) {
					newLowerBound = trueValue;
					newUpperBound = newLowerBound.add(type.getEpsilon()).subtract(roundingDelta);
				}
				newUpperBound = newUpperBound.min(refinedBid.getUpperBound()).max(trueValue);
				newLowerBound = newLowerBound.max(refinedBid.getLowerBound()).min(trueValue);

				returnBid.addBundleBid(new BundleBoundValuePair(newLowerBound, newUpperBound, refinedBid.getBundle(),
						refinedBid.getId()));
			} else {
				BigDecimal newLowerBound = refinedBid.getLowerBound();
				BigDecimal newUpperBound = refinedBid.getLowerBound().add(type.getEpsilon()).subtract(roundingDelta);
				if (newUpperBound.compareTo(trueValue) < 0) {
					newUpperBound = trueValue;
					newLowerBound = newUpperBound.subtract(type.getEpsilon()).add(roundingDelta);
				}
				newUpperBound = newUpperBound.min(refinedBid.getUpperBound()).max(trueValue);
				newLowerBound = newLowerBound.max(refinedBid.getLowerBound()).min(trueValue);

				returnBid.addBundleBid(new BundleBoundValuePair(newLowerBound, newUpperBound, refinedBid.getBundle(),
						refinedBid.getId()));
			}

		}

		return returnBid;
	}

	private List<ImmutablePair<Bundle, BigDecimal>> getDIARErrorOrder(BundleValueBid<BundleBoundValuePair> activeBids,
			Prices prices, Bundle provisionalTrade) {

		List<ImmutablePair<Bundle, BigDecimal>> diarError = new ArrayList<>();

		for (BundleBoundValuePair bid : activeBids.getBundleBids()) {

			diarError.add(new ImmutablePair<>(bid.getBundle(), this.getPerturbedValue(bid, provisionalTrade)
					.subtract(prices.getPrice(bid.getBundle()).getAmount())));
		}

		diarError.sort((e1, e2) -> -e1.getRight().compareTo(e2.getRight()));

		return diarError;
	}

	private BigDecimal getPerturbedValue(BundleBoundValuePair bid, Bundle provisionalAllocation) {
		if (bid.getBundle().equals(provisionalAllocation))
			return bid.getLowerBound();
		return bid.getUpperBound();
	}
}
