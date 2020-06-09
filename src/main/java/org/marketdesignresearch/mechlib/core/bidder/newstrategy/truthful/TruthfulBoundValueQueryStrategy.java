package org.marketdesignresearch.mechlib.core.bidder.newstrategy.truthful;

import java.util.LinkedHashSet;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBid;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.bidder.newstrategy.BoundValueQueryStrategy;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.BoundValueQuery;

import lombok.Setter;

public class TruthfulBoundValueQueryStrategy implements BoundValueQueryStrategy{

	@Setter
	private transient Bidder bidder;

	@Override
	public BundleBoundValueBid applyBoundValueStrategy(BoundValueQuery interaction) {
		return new BundleBoundValueBid(interaction.getQueriedBundles()
				.stream()
				.map(bundle -> BoundRandomHelper.getValueBoundsForBundle(bidder, bundle))
				.collect(Collectors.toCollection(LinkedHashSet::new)));
	}
	
}
