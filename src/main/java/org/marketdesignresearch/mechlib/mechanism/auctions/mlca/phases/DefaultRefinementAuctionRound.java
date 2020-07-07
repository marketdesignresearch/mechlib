package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases;

import java.util.Map;
import java.util.UUID;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBids;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.DefaultAuctionRound;

import lombok.Getter;

public class DefaultRefinementAuctionRound extends DefaultAuctionRound<BundleBoundValueBids>
		implements RefinementAuctionRound {

	@Getter
	private final BundleBoundValueBids bids;
	@Getter
	private final Map<UUID, BidderRefinementRoundInfo> refinementInfos;

	public DefaultRefinementAuctionRound(Auction<BundleBoundValueBids> auction, BundleBoundValueBids bids,
			Map<UUID, BidderRefinementRoundInfo> refinementRoundInfos) {
		super(auction);
		this.bids = bids;
		this.refinementInfos = refinementRoundInfos;
	}
}
