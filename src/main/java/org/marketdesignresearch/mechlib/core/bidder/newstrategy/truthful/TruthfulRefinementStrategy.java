package org.marketdesignresearch.mechlib.core.bidder.newstrategy.truthful;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBid;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.bidder.newstrategy.RefinementStrategy;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.RefinementQuery;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.RefinementType;

import lombok.Setter;

public class TruthfulRefinementStrategy implements RefinementStrategy {
	
	@Setter
	private transient Bidder bidder;

	@Override
	public BundleBoundValueBid applyRefinementStrategy(RefinementQuery query) {
		BundleBoundValueBid refinedBid = query.getLatestActiveBid().copy();
		
		for(RefinementType type : query.getRefinementTypes()) {
			refinedBid = AutomatedRefiner.refine(type, bidder, query.getLatestActiveBid(), refinedBid, query.getPrices(), query.getProvisonalAllocation());
		}
		return refinedBid;
	}

}
