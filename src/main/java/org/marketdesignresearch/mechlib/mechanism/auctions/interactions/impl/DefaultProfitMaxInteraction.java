package org.marketdesignresearch.mechlib.mechanism.auctions.interactions.impl;

import java.util.UUID;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBid;
import org.marketdesignresearch.mechlib.core.bidder.strategy.ProfitMaxStrategy;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.DefaultInteraction;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.ProfitMaxQuery;
import org.springframework.data.annotation.PersistenceConstructor;

import com.google.common.base.Preconditions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class DefaultProfitMaxInteraction extends DefaultInteraction<BundleExactValueBid> implements ProfitMaxQuery {

	@Getter
	private final Prices prices;
	@Getter
	private final int numberOfBids;

	@PersistenceConstructor
	protected DefaultProfitMaxInteraction(Prices prices, int numberOfBids, UUID bidderUuid) {
		super(bidderUuid);
		this.prices = prices;
		this.numberOfBids = numberOfBids;
	}

	public DefaultProfitMaxInteraction(Prices prices, int numberOfBids, UUID bidderUuid, Auction<?> auction) {
		super(bidderUuid, auction);
		this.prices = prices;
		this.numberOfBids = numberOfBids;
	}

	@Override
	public BundleExactValueBid proposeBid() {
		return this.getBidder().getStrategy(ProfitMaxStrategy.class).applyProfitMaxStrategy(this, this.getAuction());
	}

	@Override
	public void submitBid(BundleExactValueBid bid) {
		Preconditions.checkArgument(this.auction.getDomain().getGoods().containsAll(bid.getGoods()));
		Preconditions.checkArgument(bid.getBundleBids().size() <= this.getNumberOfBids());
		super.submitBid(bid);
	}
}
