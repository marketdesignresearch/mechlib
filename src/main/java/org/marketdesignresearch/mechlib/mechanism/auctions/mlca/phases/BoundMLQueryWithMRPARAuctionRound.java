package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBids;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.ElicitationEconomy;

import lombok.Getter;

public class BoundMLQueryWithMRPARAuctionRound extends MLQueryAuctionRound<BundleBoundValueBids> implements RefinementAuctionRound{

	@Getter
	private final Map<UUID, BidderRefinementRoundInfo> refinementInfos;
	
	public BoundMLQueryWithMRPARAuctionRound(Auction<BundleBoundValueBids> auction, BundleBoundValueBids bids, Map<UUID, List<ElicitationEconomy>> marginalsMap, long seed, Map<UUID, BidderRefinementRoundInfo> refinementInfos) {
		super(auction, bids, marginalsMap,seed);
		this.refinementInfos = refinementInfos;
	}
	
	public BoundMLQueryWithMRPARAuctionRound(int roundNumber, int auctionPhaseNumber, int auctionPhaseRoundNumber,
			BundleBoundValueBids bids, Map<UUID, List<ElicitationEconomy>> marginalsMap, long seed, Map<UUID, BidderRefinementRoundInfo> refinementInfos) {
		super(roundNumber, auctionPhaseNumber, auctionPhaseRoundNumber, bids, marginalsMap, seed);
		this.refinementInfos = refinementInfos;
	}
}
