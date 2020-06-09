package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.refinement.validator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValuePair;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.DIARRefinement;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.MRPARRefinement;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.RefinementType;

import com.google.common.base.Preconditions;

public class ICEValidator {
	
	@SuppressWarnings("rawtypes")
	private static Map<Class<? extends RefinementType>,ActivityRuleValidator> validators;
	
	static {
		validators = new HashMap<>();
		validators.put(DIARRefinement.class, new DIARValidator());
		validators.put(MRPARRefinement.class, new MRPARValidator());
	}
	
	@SuppressWarnings("unchecked")
	private static <E extends RefinementType> ActivityRuleValidator<E> getValidator(E type) {
		return validators.get(type.getClass());
	}
	
	public static void validateRefinement(
			BundleBoundValueBid activeBids,
			BundleBoundValueBid refinedBids,
			Prices bidderPrices,
			Bundle provisionalAllocation, Set<RefinementType> refinementType) throws ValidatorException{
		
		checkRefiementConsistency(activeBids, refinedBids);
		
		for(RefinementType type : refinementType) {
			getValidator(type).validateRefinement(type, activeBids, refinedBids, bidderPrices, provisionalAllocation);
		}
	}
	
	private static void checkRefiementConsistency(BundleValueBid<BundleBoundValuePair> activeBids, BundleValueBid<BundleBoundValuePair> refinedBids) throws ValidatorException{
		for(BundleBoundValuePair active : activeBids.getBundleBids()) {
		    Preconditions.checkState(refinedBids.getBidForBundle(active.getBundle()) != null);
			if(refinedBids.getBidForBundle(active.getBundle()).getLowerBound().compareTo(active.getLowerBound()) < 0) {
				throw new ValidatorException("Illegal bound refinement: Lowering lower bound");
			}
			if(refinedBids.getBidForBundle(active.getBundle()).getUpperBound().compareTo(active.getUpperBound()) > 0) {
				throw new ValidatorException("Illegal bound refinement: Increasing upper bound");
			}
		}
	}

}
