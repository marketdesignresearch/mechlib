package org.marketdesignresearch.mechlib.mechanism.auctions.interactions.impl;

import java.util.UUID;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.bid.demand.DemandBid;
import org.marketdesignresearch.mechlib.core.bidder.newstrategy.DemandQueryStrategy;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.DefaultInteraction;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.DemandQuery;
import org.springframework.data.annotation.PersistenceConstructor;

import com.google.common.base.Preconditions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class DefaultDemandQueryInteraction extends DefaultInteraction<DemandBid>
		implements DemandQuery {

	@Getter
	private final Prices prices;

	@PersistenceConstructor
	protected DefaultDemandQueryInteraction(UUID bidder, Prices prices) {
		super(bidder);
		this.prices = prices;
	}

	public DefaultDemandQueryInteraction(UUID bidder, Prices prices, Auction<?> auction) {
		super(bidder, auction);
		this.prices = prices;
	}


	@Override
	public void submitBid(DemandBid bid) {
		Preconditions.checkArgument(this.auction.getDomain().getGoods().containsAll(bid.getDemandedBundle()
				.getBundleEntries().stream().map(e -> e.getGood()).collect(Collectors.toList())));
		
		// TODO add Activity Rule framework
		super.submitBid(bid);
	}

	@Override
	public DemandBid proposeBid() {
		return this.getBidder().getStrategy(DemandQueryStrategy.class).applyDemandStrategy(this);
	}
}
