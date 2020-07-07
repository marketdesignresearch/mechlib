package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.refinement.validator;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBid;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.RefinementType;

public abstract class ActivityRuleValidator<E extends RefinementType> {

	public abstract void validateRefinement(E type, BundleBoundValueBid activeBids, BundleBoundValueBid refinedBids,
			Prices bidderPrices, Bundle provisionalAllocation) throws ValidatorException;

}
