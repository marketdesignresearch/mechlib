package org.marketdesignresearch.mechlib.mechanism.auctions.interactions.impl;

import java.util.UUID;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBid;
import org.marketdesignresearch.mechlib.core.bidder.newstrategy.SimpleBidStrategy;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.DefaultInteraction;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.SimpleBidInteraction;
import org.springframework.data.annotation.PersistenceConstructor;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class DefaultSimpleBidInteraction extends DefaultInteraction<BundleExactValueBid>
		implements SimpleBidInteraction {

	public DefaultSimpleBidInteraction(UUID bidder, Auction<?> auction) {
		super(bidder, auction);
	}

	@PersistenceConstructor
	protected DefaultSimpleBidInteraction(UUID bidder) {
		super(bidder);
	}

	@Override
	public BundleExactValueBid proposeBid() {
		return this.getBidder().getStrategy(SimpleBidStrategy.class).applySimpleBidStrategy(this, this.getAuction());
	}

	@Override
	public void submitBid(BundleExactValueBid bid) {
		// TODO check Preconditions
		// e.g. a bidder is might not allowed to lower his bids
		super.submitBid(bid);
	}
}
