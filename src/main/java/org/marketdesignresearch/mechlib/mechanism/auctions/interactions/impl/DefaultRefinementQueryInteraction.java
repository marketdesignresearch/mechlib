package org.marketdesignresearch.mechlib.mechanism.auctions.interactions.impl;

import java.util.Set;
import java.util.UUID;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBids;
import org.marketdesignresearch.mechlib.core.bidder.newstrategy.RefinementStrategy;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.DefaultInteraction;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.RefinementQuery;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.RefinementType;
import org.springframework.data.annotation.PersistenceConstructor;

import lombok.Getter;

public class DefaultRefinementQueryInteraction extends DefaultInteraction<BundleBoundValueBid> implements RefinementQuery {
	
	@Getter
	private final Set<RefinementType> refinementTypes;
	@Getter
	private final Bundle provisonalAllocation;
	@Getter
	private final Prices prices;
	@Getter
	private final BundleBoundValueBid latestActiveBid;
	
	@PersistenceConstructor
	protected DefaultRefinementQueryInteraction(UUID bidder, Set<RefinementType> refinementTypes, Bundle provisionalAllocation, Prices prices, BundleBoundValueBid latestActiveBids) {
		super(bidder);
		this.refinementTypes = refinementTypes;
		this.provisonalAllocation = provisionalAllocation;
		this.prices = prices;
		this.latestActiveBid = latestActiveBids;
	}
	
	public DefaultRefinementQueryInteraction(UUID bidder, Auction<BundleBoundValueBids> auction, Set<RefinementType> refinementTypes, Bundle provisionalAllocation, Prices prices, BundleBoundValueBid latestActiveBids) {
		super(bidder,auction);
		this.refinementTypes = refinementTypes;
		this.provisonalAllocation = provisionalAllocation;
		this.prices = prices;
		this.latestActiveBid = latestActiveBids;
	}

	@Override
	public BundleBoundValueBid proposeBid() {
		return this.getBidder().getStrategy(RefinementStrategy.class).applyRefinementStrategy(this, this.getAuction());
	}
}
