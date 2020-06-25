package org.marketdesignresearch.mechlib.mechanism.auctions.interactions.impl;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBid;
import org.marketdesignresearch.mechlib.core.bidder.newstrategy.BoundValueQueryStrategy;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.BoundValueQuery;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.DefaultInteraction;
import org.springframework.data.annotation.PersistenceConstructor;

import com.google.common.base.Preconditions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class DefaultBoundValueQueryInteraction extends DefaultInteraction<BundleBoundValueBid> implements BoundValueQuery {

	@Getter
	private final Set<Bundle> queriedBundles;

	@PersistenceConstructor
	protected DefaultBoundValueQueryInteraction(Set<Bundle> bundles, UUID bidder) {
		super(bidder);
		this.queriedBundles = bundles;
	}
	
	public DefaultBoundValueQueryInteraction(Set<Bundle> bundles, UUID bidder, Auction<?> auction) {
		super(bidder, auction);
		this.queriedBundles = bundles;
	}

	@Override
	public BundleBoundValueBid proposeBid() {
		return this.getBidder().getStrategy(BoundValueQueryStrategy.class).applyBoundValueStrategy(this, this.getAuction());
	}

	@Override
	public void submitBid(BundleBoundValueBid bid) {
		Preconditions.checkArgument(this.auction.getDomain().getGoods().containsAll(bid.getGoods()));
		Preconditions.checkArgument(this.getQueriedBundles().containsAll(bid.getBundleBids().stream().map(b -> b.getBundle()).collect(Collectors.toList())));
		Preconditions.checkArgument(this.getQueriedBundles().size() == bid.getBundleBids().size());
		super.submitBid(bid);
	}
}
