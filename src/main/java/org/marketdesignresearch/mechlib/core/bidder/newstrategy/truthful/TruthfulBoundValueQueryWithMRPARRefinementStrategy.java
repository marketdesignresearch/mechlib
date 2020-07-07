package org.marketdesignresearch.mechlib.core.bidder.newstrategy.truthful;

import java.util.LinkedHashSet;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBid;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.bidder.newstrategy.BoundValueQueryWithMRPARRefinementStrategy;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.BoundValueQueryWithMRPARRefinement;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.MRPARRefinement;

import lombok.Setter;

public class TruthfulBoundValueQueryWithMRPARRefinementStrategy implements BoundValueQueryWithMRPARRefinementStrategy{

	@Setter
	private transient Bidder bidder;
	
	@Override
	public BundleBoundValueBid applyBoundValueQueryWithMRPARRefinementStrategy(
			BoundValueQueryWithMRPARRefinement query) {
		BundleBoundValueBid newBids = new BundleBoundValueBid(query.getQueriedBundles()
				.stream()
				.map(bundle -> BoundRandomHelper.getValueBoundsForBundle(bidder, bundle))
				.collect(Collectors.toCollection(LinkedHashSet::new)));
		
		BundleBoundValueBid activeBids = query.getLatestActiveBid().join(newBids);
		return AutomatedRefiner.refine(new MRPARRefinement(), bidder, activeBids, activeBids, query.getPrices(), query.getProvisionalAllocation());
	}

}
