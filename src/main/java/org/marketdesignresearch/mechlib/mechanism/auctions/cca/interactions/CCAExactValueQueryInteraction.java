package org.marketdesignresearch.mechlib.mechanism.auctions.cca.interactions;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;
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
public class CCAExactValueQueryInteraction extends DefaultInteraction<BundleValueBid<BundleValuePair>, BundleValuePair> implements ExactValueQuery {

	@Getter
	private final Set<Bundle> queriedBundles;

	@PersistenceConstructor
	protected CCAExactValueQueryInteraction(Set<Bundle> bundles, UUID bidder) {
		super(bidder);
		this.queriedBundles = bundles;
	}
	
	public CCAExactValueQueryInteraction(Set<Bundle> bundles, UUID bidder, Auction<BundleValuePair> auction) {
		super(bidder, auction);
		this.queriedBundles = bundles;
	}

	@Override
	public BundleValueBid<BundleValuePair> proposeBid() {
		return this.getBidder().getStrategy(ExactValueQueryStrategy.class).applyExactValueStrategy(this);
	}

	@Override
	public void submitBid(BundleValueBid<BundleValuePair> bid) {
		Preconditions.checkArgument(this.auction.getDomain().getGoods().containsAll(bid.getGoods()));
		Preconditions.checkArgument(this.getQueriedBundles().containsAll(bid.getBundleBids().stream().map(b -> b.getBundle()).collect(Collectors.toList())));
		Preconditions.checkArgument(this.getQueriedBundles().size() == bid.getBundleBids().size());
		this.submitBid(bid);
	}
}
