package org.marketdesignresearch.mechlib.core.bidder.strategy.impl;

import java.util.LinkedHashSet;
import java.util.UUID;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValuePair;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.bidder.strategy.ExactValueQueryStrategy;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.ValueFunction;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.ExactValueQuery;

import lombok.Getter;
import lombok.Setter;

public abstract class DefaultExactValueStrategy implements ExactValueQueryStrategy {

	@Setter
	@Getter
	private transient Bidder bidder;

	public BundleExactValueBid applyExactValueStrategy(ExactValueQuery interaction, Auction<?> auction) {
		return new BundleExactValueBid(interaction.getQueriedBundles().stream().map(
				b -> new BundleExactValuePair(this.getValueFunction().getValue(b), b, UUID.randomUUID().toString()))
				.collect(Collectors.toCollection(LinkedHashSet::new)));
	}

	public abstract ValueFunction getValueFunction();
}
