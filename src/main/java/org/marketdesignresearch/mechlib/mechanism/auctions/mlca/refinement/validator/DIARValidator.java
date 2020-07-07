package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.refinement.validator;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValuePair;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.DIARRefinement;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DIARValidator extends ActivityRuleValidator<DIARRefinement>{
	
	@Override
	public void validateRefinement(DIARRefinement type,
			BundleBoundValueBid activeBids,
			BundleBoundValueBid refinedBids, Prices bidderPrices,
			Bundle provisionalAllocation) throws ValidatorException{
		
		BigDecimal epsilon = type.getEpsilon();
		BigDecimal highestErrorReduced = highestErrorReduced(activeBids, refinedBids, bidderPrices, provisionalAllocation, epsilon);
		BigDecimal highestErrorReductionPossible = highestErrorReductionPossible(activeBids, refinedBids, bidderPrices, provisionalAllocation, epsilon);
		boolean result = highestErrorReduced.compareTo(highestErrorReductionPossible) >= 0;
		
		if(!result) {
			log.error("HighestErrorReduced "+highestErrorReduced.setScale(6, RoundingMode.HALF_UP) + " vs. HighestErrorReductionPossible "+ highestErrorReductionPossible.setScale(6, RoundingMode.HALF_UP));
			throw new ValidatorException("DIAR validation failed");
		}
	}
	
	private BigDecimal highestErrorReductionPossible(BundleValueBid<BundleBoundValuePair> activeBids,
			BundleValueBid<BundleBoundValuePair> refinedBids, Prices bidderPrices,
			Bundle provisionalAllocation, BigDecimal epsilon) {
		
		BigDecimal provisionalError = BigDecimal.ZERO;
		if(provisionalAllocation != null && !(provisionalAllocation.getTotalAmount() == 0))
			provisionalError = activeBids.getBidForBundle(provisionalAllocation).getLowerBound().subtract(bidderPrices.getPrice(provisionalAllocation).getAmount());
				
		BigDecimal highestErrorImproved = BigDecimal.valueOf(-Double.MAX_VALUE);
		for(BundleBoundValuePair activeBid : activeBids.getBundleBids()) {
			BigDecimal error = this.perturbedValuation(activeBid, provisionalAllocation).subtract(bidderPrices.getPrice(activeBid.getBundle()).getAmount()).subtract(provisionalError);
			
			if(error.compareTo(highestErrorImproved) > 0) {
				if(!activeBid.getBundle().equals(provisionalAllocation)) {
					BigDecimal range = refinedBids.getBidForBundle(activeBid.getBundle()).getUpperBound().subtract(refinedBids.getBidForBundle(activeBid.getBundle()).getLowerBound());
					if(provisionalAllocation != null && !( provisionalAllocation.getTotalAmount() == 0)) {
						range = range.add(refinedBids.getBidForBundle(provisionalAllocation).getUpperBound().subtract(refinedBids.getBidForBundle(provisionalAllocation).getLowerBound()));
					}
					if(range.compareTo(epsilon) >= 0) {
						highestErrorImproved = error;
					}
				}
			}
		}
		
		// empty bundle
		if(provisionalAllocation != null && ! (provisionalAllocation.getTotalAmount() == 0)) {
			BigDecimal error = BigDecimal.ZERO.subtract((activeBids.getBidForBundle(provisionalAllocation).getLowerBound().subtract(bidderPrices.getPrice(provisionalAllocation).getAmount())));
			if(error.compareTo(highestErrorImproved) > 0) {
				BigDecimal range = refinedBids.getBidForBundle(provisionalAllocation).getUpperBound().subtract(refinedBids.getBidForBundle(provisionalAllocation).getLowerBound());
				if(range.compareTo(epsilon) >= 0) {
					highestErrorImproved = error;
				}
			}
		}
		
		return highestErrorImproved;
	}
	
	private BigDecimal highestErrorReduced(BundleValueBid<BundleBoundValuePair> activeBids,
			BundleValueBid<BundleBoundValuePair> refinedBids, Prices bidderPrices,
			Bundle provisionalAllocation, BigDecimal epsilon) {
		BigDecimal highestErrorImproved = BigDecimal.valueOf(-Double.MAX_VALUE);
		
		BigDecimal provisionalError = BigDecimal.ZERO;
		if(provisionalAllocation != null && !(provisionalAllocation.getTotalAmount() == 0))
			provisionalError = activeBids.getBidForBundle(provisionalAllocation).getLowerBound().subtract(bidderPrices.getPrice(provisionalAllocation).getAmount());
		
		
		for(BundleBoundValuePair activeBid : activeBids.getBundleBids()) {
			BigDecimal error = this.perturbedValuation(activeBid, provisionalAllocation).subtract(bidderPrices.getPrice(activeBid.getBundle()).getAmount()).subtract(provisionalError);
			
			if(error.compareTo(highestErrorImproved) > 0) {
				BigDecimal errorReduction = this.perturbedValuation(activeBid, provisionalAllocation)
						.subtract(this.perturbedValuation(refinedBids.getBidForBundle(activeBid.getBundle()), provisionalAllocation));
				if(provisionalAllocation != null && ! (provisionalAllocation.getTotalAmount() == 0))
					errorReduction = errorReduction
							.subtract(activeBids.getBidForBundle(provisionalAllocation).getLowerBound())
							.add(refinedBids.getBidForBundle(provisionalAllocation).getLowerBound());
				
				if(errorReduction.compareTo(epsilon) >= 0) {
					highestErrorImproved = error;
				}
			}
		}
		
		// empty bundle
		if(provisionalAllocation != null && ! (provisionalAllocation.getTotalAmount() == 0)) {
			BigDecimal error = BigDecimal.ZERO.subtract(activeBids.getBidForBundle(provisionalAllocation).getLowerBound().subtract(bidderPrices.getPrice(provisionalAllocation).getAmount()));
			if(error.compareTo(highestErrorImproved) > 0) {
				BigDecimal errorReduction = BigDecimal.ZERO.subtract(activeBids.getBidForBundle(provisionalAllocation).getLowerBound()).add(refinedBids.getBidForBundle(provisionalAllocation).getLowerBound());
				if(errorReduction.compareTo(epsilon) >= 0) {
					highestErrorImproved = error;
				}
			}
		}
		
		return highestErrorImproved;
	}
	
	private BigDecimal perturbedValuation(BundleBoundValuePair bid, Bundle withRespectTo) {
		if(bid.getBundle().equals(withRespectTo)) {
			return bid.getLowerBound();
		}
		return bid.getUpperBound();
	}
}
