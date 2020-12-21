package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.refinement.validator;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValuePair;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.DIARVariant1Refinement;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DIARVariant1Validator extends ActivityRuleValidator<DIARVariant1Refinement>{

	@Override
	public void validateRefinement(DIARVariant1Refinement type, BundleBoundValueBid activeBids, BundleBoundValueBid refinedBids,
			Prices bidderPrices, Bundle provisionalAllocation) throws ValidatorException {

		BigDecimal epsilon = type.getEpsilon();
		BigDecimal highestErrorReduced = highestErrorReduced(activeBids, refinedBids, bidderPrices,
				provisionalAllocation, epsilon);
		BigDecimal highestErrorReductionPossible = highestErrorReductionPossible(activeBids, refinedBids, bidderPrices,
				provisionalAllocation, epsilon);
		boolean result = highestErrorReduced.compareTo(highestErrorReductionPossible) >= 0;

		if (!result) {
			log.error("HighestErrorReduced " + highestErrorReduced.setScale(6, RoundingMode.HALF_UP)
					+ " vs. HighestErrorReductionPossible "
					+ highestErrorReductionPossible.setScale(6, RoundingMode.HALF_UP));
			throw new ValidatorException("DIAR validation failed");
		}
	}

	private BigDecimal highestErrorReductionPossible(BundleValueBid<BundleBoundValuePair> activeBids,
			BundleValueBid<BundleBoundValuePair> refinedBids, Prices bidderPrices, Bundle provisionalAllocation,
			BigDecimal epsilon) {

		BigDecimal highestErrorReductionPossible = BigDecimal.valueOf(-Double.MAX_VALUE);
		for (BundleBoundValuePair activeBid : activeBids.getBundleBids()) {
			BigDecimal error = this.perturbedValuation(activeBid, provisionalAllocation)
					.subtract(bidderPrices.getPrice(activeBid.getBundle()).getAmount());

			if (error.compareTo(highestErrorReductionPossible) > 0) {
					BigDecimal range = refinedBids.getBidForBundle(activeBid.getBundle()).getUpperBound()
							.subtract(refinedBids.getBidForBundle(activeBid.getBundle()).getLowerBound());
					if (range.compareTo(epsilon) >= 0) {
						highestErrorReductionPossible = error;
				}
			}
		}

		return highestErrorReductionPossible;
	}

	private BigDecimal highestErrorReduced(BundleValueBid<BundleBoundValuePair> activeBids,
			BundleValueBid<BundleBoundValuePair> refinedBids, Prices bidderPrices, Bundle provisionalAllocation,
			BigDecimal epsilon) {
		BigDecimal highestErrorImproved = BigDecimal.valueOf(-Double.MAX_VALUE);

		for (BundleBoundValuePair activeBid : activeBids.getBundleBids()) {
			BigDecimal error = this.perturbedValuation(activeBid, provisionalAllocation)
					.subtract(bidderPrices.getPrice(activeBid.getBundle()).getAmount());

			if (error.compareTo(highestErrorImproved) > 0) {
				BigDecimal errorReduction;
				if(activeBid.getBundle().equals(provisionalAllocation)) {
					errorReduction = refinedBids.getBidForBundle(activeBid.getBundle()).getLowerBound().subtract(activeBid.getLowerBound());
				} else {
					errorReduction = activeBid.getUpperBound().subtract(refinedBids.getBidForBundle(activeBid.getBundle()).getUpperBound());
				}
				if (errorReduction.compareTo(epsilon) >= 0) {
					highestErrorImproved = error;
				}
			}
		}

		return highestErrorImproved;
	}

	private BigDecimal perturbedValuation(BundleBoundValuePair bid, Bundle withRespectTo) {
		if (bid.getBundle().equals(withRespectTo)) {
			return bid.getLowerBound();
		}
		return bid.getUpperBound();
	}

}
