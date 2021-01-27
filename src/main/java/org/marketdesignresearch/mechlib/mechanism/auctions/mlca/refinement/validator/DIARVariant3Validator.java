package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.refinement.validator;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValuePair;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.DIARVariant3Refinement;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DIARVariant3Validator extends ActivityRuleValidator<DIARVariant3Refinement>{

	@Override
	public void validateRefinement(DIARVariant3Refinement type, BundleBoundValueBid activeBids, BundleBoundValueBid refinedBids,
			Prices bidderPrices, Bundle provisionalAllocation) throws ValidatorException {

		for(Bundle b : type.getBundles()) {
			BundleBoundValuePair refinedBid = refinedBids.getBidForBundle(b);
			if(refinedBid.getUpperBound().subtract(refinedBid.getLowerBound()).compareTo(type.getEpsilon())>0) {
				log.error("DIAR Validation failed for bundle: "+b);
				throw new ValidatorException("DIAR validation failed");
			}
		}
	}


}
