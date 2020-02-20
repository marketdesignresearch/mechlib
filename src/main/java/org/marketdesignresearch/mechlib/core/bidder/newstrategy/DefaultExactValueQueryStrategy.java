package org.marketdesignresearch.mechlib.core.bidder.newstrategy;

import java.util.LinkedHashSet;
import java.util.UUID;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.ExactValueQuery;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultExactValueQueryStrategy
		implements InteractionStrategy<BundleValueBid<BundleValuePair>, ExactValueQuery> {
	private final Bidder bidder;

	@Override
	public BundleValueBid<BundleValuePair> applyStrategy(ExactValueQuery interaction) {
		return new BundleValueBid<>(interaction.getQueriedBundles().stream()
				.map(b -> new BundleValuePair(this.bidder.getValue(b), b, UUID.randomUUID().toString()))
				.collect(Collectors.toCollection(LinkedHashSet::new)));
	}
}
