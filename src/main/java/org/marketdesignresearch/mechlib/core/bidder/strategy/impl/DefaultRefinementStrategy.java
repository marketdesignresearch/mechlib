package org.marketdesignresearch.mechlib.core.bidder.strategy.impl;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBid;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.bidder.random.BidderRandom;
import org.marketdesignresearch.mechlib.core.bidder.strategy.RefinementStrategy;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.ValueFunction;
import org.marketdesignresearch.mechlib.instrumentation.RefinementInstrumentable;
import org.marketdesignresearch.mechlib.instrumentation.RefinementInstrumentation;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.RefinementQuery;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.RefinementType;

import lombok.Getter;
import lombok.Setter;

public abstract class DefaultRefinementStrategy implements RefinementStrategy, RefinementInstrumentable {
	@Getter
	@Setter
	private transient Bidder bidder;

	@Setter
	@Getter
	private RefinementInstrumentation refinementInstrumentation = RefinementInstrumentation.NO_OP;

	@Override
	public BundleBoundValueBid applyRefinementStrategy(RefinementQuery query, Auction<?> auction) {
		BundleBoundValueBid refinedBid = query.getLatestActiveBid().copy();

		for (RefinementType type : query.getRefinementTypes()) {
			this.getRefinementInstrumentation().preRefinement(type, getBidder(), query.getLatestActiveBid(), refinedBid,
					query.getPrices(), query.getProvisonalAllocation());
			refinedBid = AutomatedRefiner.refine(type, this.getValueFunction(), query.getLatestActiveBid(), refinedBid,
					query.getPrices(), query.getProvisonalAllocation(), BidderRandom.INSTANCE.getRandom());
			this.getRefinementInstrumentation().postRefinement(type, this.getBidder(), query.getLatestActiveBid(),
					refinedBid, query.getPrices(), query.getProvisonalAllocation());
		}
		return refinedBid;
	}

	public abstract ValueFunction getValueFunction();

}
