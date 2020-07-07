package org.marketdesignresearch.mechlib.mechanism.auctions.interactions.impl;

import java.util.Set;
import java.util.UUID;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBid;
import org.marketdesignresearch.mechlib.core.bidder.newstrategy.BoundValueQueryWithMRPARRefinementStrategy;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.BoundValueQueryWithMRPARRefinement;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.DefaultInteraction;
import org.springframework.data.annotation.PersistenceConstructor;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class DefaultBoundValueQueryWithMRPARRefinementInteraction extends DefaultInteraction<BundleBoundValueBid> implements BoundValueQueryWithMRPARRefinement{
	
	@Getter
	private final Bundle provisionalAllocation;
	@Getter
	private final Prices prices;
	@Getter
	private final BundleBoundValueBid latestActiveBid;
	@Getter
	private final Set<Bundle> queriedBundles;
	
	@PersistenceConstructor
	protected DefaultBoundValueQueryWithMRPARRefinementInteraction(UUID bidderUuid, Bundle provisionalAllocation, Prices prices, BundleBoundValueBid bid, Set<Bundle> queriedBundles) {
		super(bidderUuid);
		this.provisionalAllocation = provisionalAllocation;
		this.prices = prices;
		this.latestActiveBid = bid;
		this.queriedBundles = queriedBundles;
	}
	
	public DefaultBoundValueQueryWithMRPARRefinementInteraction(UUID bidderUuid, Auction<?> auction, Bundle provisionalAllocation, Prices prices, BundleBoundValueBid bid, Set<Bundle> queriedBundles) {
		super(bidderUuid,auction);
		this.provisionalAllocation = provisionalAllocation;
		this.prices = prices;
		this.latestActiveBid = bid;
		this.queriedBundles = queriedBundles;
	}

	@Override
	public BundleBoundValueBid proposeBid() {
		return this.getBidder().getStrategy(BoundValueQueryWithMRPARRefinementStrategy.class).applyBoundValueQueryWithMRPARRefinementStrategy(this);
	}
}
