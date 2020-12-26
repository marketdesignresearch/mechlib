package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.refinement;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashSet;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValuePair;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.DIARRefinement;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.MRPARRefinement;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.RefinementType;

public class MRPAR_DIAR_RefinementRoundInfoCreator extends BidderRefinementRoundInfoCreator {

	@Override
	protected LinkedHashSet<RefinementType> createRefinementType(BundleBoundValueBids bids, Allocation alphaAllocation,
			Prices pi) {
		// Linked Hash set - the order of the refinement is deterministic
		LinkedHashSet<RefinementType> refinements = new LinkedHashSet<>();
		refinements.add(new MRPARRefinement());
		refinements.add(new DIARRefinement(calulateDIAREpsilon(bids)));
		return refinements;
	}

	private BigDecimal calulateDIAREpsilon(BundleBoundValueBids bids) {
		BigDecimal epsilon = BigDecimal.ZERO;
		for (Bidder b : bids.getBidders()) {
			BundleBoundValueBid bid = bids.getBid(b);
			for (BundleBoundValuePair value : bid.getBundleBids()) {
				epsilon = epsilon.add(value.getUpperBound().subtract(value.getLowerBound())
						.divide(BigDecimal.valueOf(bid.getBundleBids().size()), RoundingMode.HALF_UP));
			}
		}
		epsilon = epsilon.divide(BigDecimal.valueOf(2 * bids.getBidders().size()), RoundingMode.HALF_UP);
		return epsilon;
	}

}
