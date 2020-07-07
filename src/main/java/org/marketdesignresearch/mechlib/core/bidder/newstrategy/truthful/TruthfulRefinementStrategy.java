package org.marketdesignresearch.mechlib.core.bidder.newstrategy.truthful;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBid;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.bidder.newstrategy.RefinementStrategy;
import org.marketdesignresearch.mechlib.instrumentation.RefinementInstrumentable;
import org.marketdesignresearch.mechlib.instrumentation.RefinementInstrumentation;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.RefinementQuery;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.RefinementType;

import lombok.Getter;
import lombok.Setter;

public class TruthfulRefinementStrategy implements RefinementStrategy, RefinementInstrumentable {

	@Setter
	private transient Bidder bidder;
	@Setter
	@Getter
	private RefinementInstrumentation refinementInstrumentation = RefinementInstrumentation.NO_OP;

	@Override
	public BundleBoundValueBid applyRefinementStrategy(RefinementQuery query, Auction<?> auction) {
		BundleBoundValueBid refinedBid = query.getLatestActiveBid().copy();

		for (RefinementType type : query.getRefinementTypes()) {
			this.getRefinementInstrumentation().preRefinement(type, bidder, query.getLatestActiveBid(), refinedBid,
					query.getPrices(), query.getProvisonalAllocation());
			refinedBid = AutomatedRefiner.refine(type, bidder, query.getLatestActiveBid(), refinedBid,
					query.getPrices(), query.getProvisonalAllocation(), auction.getCurrentRoundRandom());
			this.getRefinementInstrumentation().postRefinement(type, bidder, query.getLatestActiveBid(), refinedBid,
					query.getPrices(), query.getProvisonalAllocation());
		}
		return refinedBid;
	}

}
