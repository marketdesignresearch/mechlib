package org.marketdesignresearch.mechlib.core.bidder.strategy.truthful;

import java.util.LinkedHashSet;
import java.util.UUID;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValuePair;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.bidder.strategy.ExactValueQueryStrategy;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.ExactValueQuery;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class TruthfulExactValueQueryStrategy implements ExactValueQueryStrategy {

	@Setter
	private transient Bidder bidder;

	@Override
	public BundleExactValueBid applyExactValueStrategy(ExactValueQuery interaction, Auction<?> auction) {
		return new BundleExactValueBid(interaction.getQueriedBundles().stream()
				.map(b -> new BundleExactValuePair(this.bidder.getValue(b), b, UUID.randomUUID().toString()))
				.collect(Collectors.toCollection(LinkedHashSet::new)));
	}
}
