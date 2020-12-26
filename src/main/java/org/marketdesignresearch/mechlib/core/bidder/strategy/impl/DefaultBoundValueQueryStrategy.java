package org.marketdesignresearch.mechlib.core.bidder.strategy.impl;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValuePair;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.bidder.random.BidderRandom;
import org.marketdesignresearch.mechlib.core.bidder.strategy.BoundValueQueryStrategy;
import org.marketdesignresearch.mechlib.core.bidder.strategy.BoundValueQueryWithMRPARRefinementStrategy;
import org.marketdesignresearch.mechlib.core.bidder.strategy.InteractionStrategy;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.ValueFunction;
import org.marketdesignresearch.mechlib.instrumentation.RefinementInstrumentable;
import org.marketdesignresearch.mechlib.instrumentation.RefinementInstrumentation;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.BoundValueQuery;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.BoundValueQueryWithMRPARRefinement;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.MRPARRefinement;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public abstract class DefaultBoundValueQueryStrategy implements BoundValueQueryStrategy, BoundValueQueryWithMRPARRefinementStrategy, RefinementInstrumentable {

	private final static BigDecimal defaultStdDeviation = BigDecimal.valueOf(0.5);

	@Setter
	@Getter
	private transient Bidder bidder;

	private final BigDecimal stdDeviation;
	@Setter
	@Getter
	private RefinementInstrumentation refinementInstrumentation;

	public DefaultBoundValueQueryStrategy() {
		this(defaultStdDeviation);
	}

	@Override
	public BundleBoundValueBid applyBoundValueStrategy(BoundValueQuery interaction, Auction<?> auction) {
		return new BundleBoundValueBid(interaction.getQueriedBundles().stream().map(
				bundle -> this.getValueBoundsForBundle(bundle, BidderRandom.INSTANCE.getRandom(), stdDeviation))
				.collect(Collectors.toCollection(LinkedHashSet::new)));
	}

	public BundleBoundValueBid applyBoundValueQueryWithMRPARRefinementStrategy(BoundValueQueryWithMRPARRefinement query,
			Auction<?> auction) {
		BundleBoundValueBid newBids = this.applyBoundValueStrategy(query, auction);

		BundleBoundValueBid activeBids = query.getLatestActiveBid().join(newBids);

		MRPARRefinement type = new MRPARRefinement();
		this.getRefinementInstrumentation().preRefinement(type, bidder, activeBids, activeBids, query.getPrices(),
				query.getProvisionalAllocation());
		BundleBoundValueBid bids = AutomatedRefiner.refine(type, this.getValueFunction(), activeBids, activeBids, query.getPrices(),
				query.getProvisionalAllocation(), BidderRandom.INSTANCE.getRandom());
		this.getRefinementInstrumentation().postRefinement(type, bidder, activeBids, bids, query.getPrices(),
				query.getProvisionalAllocation());
		return bids;
	}

	@Override
	public Set<Class<? extends InteractionStrategy>> getTypes() {
		Set<Class<? extends InteractionStrategy>> types = new LinkedHashSet<>();
		types.addAll(BoundValueQueryStrategy.super.getTypes());
		types.addAll(BoundValueQueryWithMRPARRefinementStrategy.super.getTypes());
		return types;
	}

	/**
	 * Generate random truthful bounds
	 * 
	 * @param bundle
	 * @return
	 */
	private BundleBoundValuePair getValueBoundsForBundle(Bundle bundle, Random random,
			BigDecimal stdDeviation) {

		BigDecimal value = this.getValueFunction().getValue(bundle);
		BigDecimal lowerBound = value
				.subtract(BigDecimal.valueOf(random.nextGaussian()).abs().multiply(stdDeviation.multiply(value)))
				.max(BigDecimal.ZERO);
		BigDecimal upperBound = value
				.add(BigDecimal.valueOf(random.nextGaussian()).abs().multiply(stdDeviation.multiply(value)));
		return new BundleBoundValuePair(lowerBound, upperBound, bundle, UUID.randomUUID().toString());
	}

	public abstract ValueFunction getValueFunction();
}