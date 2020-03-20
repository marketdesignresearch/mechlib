package org.marketdesignresearch.mechlib.core.bidder.newstrategy.truthful;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValuePair;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.bidder.newstrategy.ProfitMaxStrategy;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.ProfitMaxQuery;

import com.google.common.collect.Sets;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class TruthfulProfitMaxQueryStrategy implements ProfitMaxStrategy{

	@Setter
	private transient Bidder bidder;
	
	@Override
	public BundleExactValueBid applyProfitMaxStrategy(ProfitMaxQuery interaction) {
		 List<Bundle> bestBundles = bidder.getBestBundles(interaction.getPrices(), interaction.getNumberOfBids(), true);
	     List<BundleExactValuePair> bestBundleBids = new ArrayList<>();
	     for (Bundle bundle : bestBundles) {
	         bestBundleBids.add(new BundleExactValuePair(bidder.getValue(bundle), bundle, UUID.randomUUID().toString()));
	     }
	     return new BundleExactValueBid(Sets.newHashSet(bestBundleBids));
	}

}
