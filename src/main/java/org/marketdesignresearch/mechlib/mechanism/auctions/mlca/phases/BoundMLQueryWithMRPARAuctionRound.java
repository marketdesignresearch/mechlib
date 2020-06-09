package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBids;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.ElicitationEconomy;

public class BoundMLQueryWithMRPARAuctionRound extends MLQueryAuctionRound<BundleBoundValueBids>{

	public BoundMLQueryWithMRPARAuctionRound(Auction<BundleBoundValueBids> auction, BundleBoundValueBids bids, Map<UUID, List<ElicitationEconomy>> marginalsMap, long seed) {
		super(auction, bids, marginalsMap,seed);
	}
	
	public BoundMLQueryWithMRPARAuctionRound(int roundNumber, int auctionPhaseNumber, int auctionPhaseRoundNumber,
			BundleBoundValueBids bids, Map<UUID, List<ElicitationEconomy>> marginalsMap, long seed) {
		super(roundNumber, auctionPhaseNumber, auctionPhaseRoundNumber, bids, marginalsMap, seed);
	}
}
