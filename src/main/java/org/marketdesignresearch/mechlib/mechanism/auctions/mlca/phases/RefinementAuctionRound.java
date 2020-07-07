package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases;

import java.util.Map;
import java.util.UUID;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBids;

public interface RefinementAuctionRound {
	BundleBoundValueBids getBids();

	Map<UUID, BidderRefinementRoundInfo> getRefinementInfos();
}
