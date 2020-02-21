package org.marketdesignresearch.mechlib.core.bidder.newstrategy.truthful;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.bidder.newstrategy.ProfitMaxStrategy;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.ProfitMaxQuery;

import com.google.common.collect.Sets;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TruthfulProfitMaxQueryStrategy implements ProfitMaxStrategy{

	private final Bidder bidder;
	
	@Override
	public BundleValueBid<BundleValuePair> applyProfitMaxStrategy(ProfitMaxQuery interaction) {
		 List<Bundle> bestBundles = bidder.getBestBundles(interaction.getPrices(), interaction.getNumberOfBids(), true);
	     List<BundleValuePair> bestBundleBids = new ArrayList<>();
	     for (Bundle bundle : bestBundles) {
	         bestBundleBids.add(new BundleValuePair(bidder.getValue(bundle), bundle, UUID.randomUUID().toString()));
	     }
	     return new BundleValueBid<BundleValuePair>(Sets.newHashSet(bestBundleBids));
	}

}
