package org.marketdesignresearch.mechlib.mechanism.auctions.cca.interactions;

import java.util.UUID;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;
import org.marketdesignresearch.mechlib.core.bidder.newstrategy.ProfitMaxStrategy;
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
public class CCAProfitMaxInteraction extends DefaultInteraction<BundleValueBid<BundleValuePair>, BundleValuePair> implements ProfitMaxQuery{

	@Getter
	private final Prices prices;
	@Getter
	private final int numberOfBids;
	
	@PersistenceConstructor
	protected CCAProfitMaxInteraction(Prices prices, int numberOfBids, UUID bidderUuid) {
		super(bidderUuid);
		this.prices = prices;
		this.numberOfBids = numberOfBids;
	}
	
	public CCAProfitMaxInteraction(Prices prices, int numberOfBids, UUID bidderUuid, Auction<BundleValuePair> auction) {
		super(bidderUuid, auction);
		this.prices = prices;
		this.numberOfBids = numberOfBids;
	}

	@Override
	public BundleValueBid<BundleValuePair> proposeBid() {
		return this.getBidder().getStrategy(ProfitMaxStrategy.class).applyProfitMaxStrategy(this);
	}

	@Override
	public void submitBid(BundleValueBid<BundleValuePair> bid) {
		Preconditions.checkArgument(this.auction.getDomain().getGoods().containsAll(bid.getGoods()));
		Preconditions.checkArgument(bid.getBundleBids().size() <= this.getNumberOfBids());
		super.submitBid(bid);
	}
}
