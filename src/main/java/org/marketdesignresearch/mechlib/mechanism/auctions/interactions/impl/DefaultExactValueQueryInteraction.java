package org.marketdesignresearch.mechlib.mechanism.auctions.interactions.impl;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValuePair;
import org.marketdesignresearch.mechlib.core.bidder.newstrategy.ExactValueQueryStrategy;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.DefaultInteraction;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.ExactValueQuery;
import org.springframework.data.annotation.PersistenceConstructor;

import com.google.common.base.Preconditions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class DefaultExactValueQueryInteraction extends DefaultInteraction<BundleExactValueBid> implements ExactValueQuery {

	@Getter
	private final Set<Bundle> queriedBundles;
	@Getter
	private final Bundle alreadyWon;

	protected DefaultExactValueQueryInteraction(Set<Bundle> bundles, UUID bidder) {
		this(bundles, bidder, Bundle.EMPTY);
	}

	@PersistenceConstructor
	protected DefaultExactValueQueryInteraction(Set<Bundle> bundles, UUID bidder, Bundle alreadyWon) {
		super(bidder);
		this.queriedBundles = bundles;
		this.alreadyWon = alreadyWon;
	}

	public DefaultExactValueQueryInteraction(Set<Bundle> bundles, UUID bidder, Auction<?> auction) {
		this(bundles, bidder, auction, Bundle.EMPTY);
	}
	
	public DefaultExactValueQueryInteraction(Set<Bundle> bundles, UUID bidder, Auction<?> auction, Bundle alreadyWon) {
		super(bidder, auction);
		this.queriedBundles = bundles;
		this.alreadyWon = alreadyWon;
	}

	@Override
	public BundleExactValueBid proposeBid() {
		return this.getBidder().getStrategy(ExactValueQueryStrategy.class).applyExactValueStrategy(this);
	}

	@Override
	public void submitBid(BundleExactValueBid bid) {
		Preconditions.checkArgument(this.auction.getDomain().getGoods().containsAll(bid.getGoods()));
		Preconditions.checkArgument(this.getQueriedBundles().containsAll(bid.getBundleBids().stream().map(BundleExactValuePair::getBundle).collect(Collectors.toList())));
		Preconditions.checkArgument(this.getQueriedBundles().size() == bid.getBundleBids().size());
		super.submitBid(bid);
	}
}
