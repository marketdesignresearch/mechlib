package org.marketdesignresearch.mechlib.mechanism.auctions.interactions.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValuePair;
import org.marketdesignresearch.mechlib.core.bidder.strategy.ConvergenceStrategy;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.ConvergenceInteraction;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.DefaultInteraction;

import com.google.common.base.Preconditions;

import lombok.Getter;

public class DefaultConvergenceInteraction extends DefaultInteraction<BundleBoundValueBid>
		implements ConvergenceInteraction {

	@Getter
	private final Set<Bundle> bundles;
	@Getter
	private final BigDecimal epsilon;
	@Getter
	private final BundleBoundValueBid latestActiveBid;

	public DefaultConvergenceInteraction(UUID bidderUuid, Set<Bundle> bundles, BigDecimal epsilon,
			BundleBoundValueBid latestActiveBid) {
		super(bidderUuid);
		this.bundles = bundles;
		this.epsilon = epsilon;
		this.latestActiveBid = latestActiveBid;
	}

	public DefaultConvergenceInteraction(UUID bidderUuid, Auction<?> auction, Set<Bundle> bundles, BigDecimal epsilon,
			BundleBoundValueBid latestActiveBid) {
		super(bidderUuid, auction);
		this.bundles = bundles;
		this.epsilon = epsilon;
		this.latestActiveBid = latestActiveBid;
	}

	@Override
	public BundleBoundValueBid proposeBid() {
		return this.getBidder().getStrategy(ConvergenceStrategy.class).applyConvergenceStrategy(this,
				this.getAuction());
	}

	@Override
	public void submitBid(BundleBoundValueBid bid) {
		Preconditions.checkArgument(this.auction.getDomain().getGoods().containsAll(bid.getGoods()));
		Preconditions.checkArgument(this.bundles
				.containsAll(bid.getBundleBids().stream().map(b -> b.getBundle()).collect(Collectors.toList())));
		Preconditions.checkArgument(this.bundles.size() == bid.getBundleBids().size());

		for (BundleBoundValuePair pair : bid.getBundleBids()) {
			BigDecimal interval = pair.getUpperBound().subtract(pair.getLowerBound());
			if (pair.getUpperBound().compareTo(BigDecimal.ZERO) > 0) {
				BigDecimal uncertainty = interval.divide(pair.getUpperBound(), epsilon.scale() + 1, RoundingMode.DOWN);
				// add 2% slack
				Preconditions.checkArgument(uncertainty.compareTo(epsilon.multiply(BigDecimal.valueOf(1.02))) <= 0);
			}
		}

		super.submitBid(bid);
	}
}
