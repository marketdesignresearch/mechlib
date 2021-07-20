package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.refinement;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.MRPARRefinement;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.RefinementType;

public class MRPARRefinementRoundInfoCreator extends BidderRefinementRoundInfoCreator {

	@Override
	protected LinkedHashMap<Bidder, LinkedHashSet<RefinementType>> createRefinementType(BundleBoundValueBids bids,
			Allocation alphaAllocation, Prices pi) {

		LinkedHashMap<Bidder, LinkedHashSet<RefinementType>> result = new LinkedHashMap<>();

		for (Bidder b : bids.getBidders()) {
			result.put(b, new LinkedHashSet<>(List.of(new MRPARRefinement())));
		}

		return result;
	}

}
