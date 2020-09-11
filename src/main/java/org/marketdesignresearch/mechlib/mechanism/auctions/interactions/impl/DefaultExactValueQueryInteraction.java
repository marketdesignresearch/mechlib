package org.marketdesignresearch.mechlib.mechanism.auctions.interactions.impl;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBid;
import org.marketdesignresearch.mechlib.core.bidder.strategy.ExactValueQueryStrategy;
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
public class DefaultExactValueQueryInteraction extends DefaultInteraction<BundleExactValueBid>
		implements ExactValueQuery {

	@Getter
	private final Set<Bundle> queriedBundles;

	@PersistenceConstructor
	protected DefaultExactValueQueryInteraction(Set<Bundle> bundles, UUID bidder) {
		super(bidder);
		this.queriedBundles = bundles;
	}

	public DefaultExactValueQueryInteraction(Set<Bundle> bundles, UUID bidder, Auction<?> auction) {
		super(bidder, auction);
		this.queriedBundles = bundles;
	}

	@Override
	public BundleExactValueBid proposeBid() {
		return this.getBidder().getStrategy(ExactValueQueryStrategy.class).applyExactValueStrategy(this,
				this.getAuction());
	}

	@Override
	public void submitBid(BundleExactValueBid bid) {
		Preconditions.checkArgument(this.auction.getDomain().getGoods().containsAll(bid.getGoods()));
		Preconditions.checkArgument(this.getQueriedBundles()
				.containsAll(bid.getBundleBids().stream().map(b -> b.getBundle()).collect(Collectors.toList())));
		Preconditions.checkArgument(this.getQueriedBundles().size() == bid.getBundleBids().size());
		super.submitBid(bid);
	}
}
