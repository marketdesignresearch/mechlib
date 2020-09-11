package org.marketdesignresearch.mechlib.core.bidder.strategy.truthful;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValuePair;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.bidder.strategy.ProfitMaxStrategy;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.ProfitMaxQuery;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@RequiredArgsConstructor
@Log4j2
public class TruthfulProfitMaxQueryStrategy implements ProfitMaxStrategy {

	@Setter
	private transient Bidder bidder;

	@Override
	public BundleExactValueBid applyProfitMaxStrategy(ProfitMaxQuery interaction, Auction<?> auction) {
		Set<Bundle> bestBundles = bidder.getBestBundles(interaction.getPrices(), interaction.getNumberOfBids(), true);
		Set<BundleExactValuePair> bestBundleBids = new LinkedHashSet<>();
		for (Bundle bundle : bestBundles) {
			bestBundleBids.add(new BundleExactValuePair(bidder.getValue(bundle), bundle, UUID.randomUUID().toString()));
		}
		log.info("Bidder: {} demand query for {} bundles and reports {} bundles", bidder.getName(),
				interaction.getNumberOfBids(), bestBundles.size());
		return new BundleExactValueBid(bestBundleBids);
	}

}
