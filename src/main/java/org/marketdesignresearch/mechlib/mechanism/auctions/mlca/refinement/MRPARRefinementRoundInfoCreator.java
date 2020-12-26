package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.refinement;

import java.util.LinkedHashSet;
import java.util.List;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBids;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.MRPARRefinement;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.RefinementType;

public class MRPARRefinementRoundInfoCreator extends BidderRefinementRoundInfoCreator{

	@Override
	protected LinkedHashSet<RefinementType> createRefinementType(BundleBoundValueBids bids, Allocation alphaAllocation,
			Prices pi) {
		return new LinkedHashSet<>(List.of(new MRPARRefinement()));
	}

}
