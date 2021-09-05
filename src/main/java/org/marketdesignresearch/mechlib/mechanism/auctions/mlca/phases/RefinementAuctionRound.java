package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases;

import java.util.Map;
import java.util.UUID;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBids;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.refinement.BidderRefinementRoundInfo;

public interface RefinementAuctionRound {
	BundleBoundValueBids getBids();

	Map<UUID, BidderRefinementRoundInfo> getRefinementInfos();
}
