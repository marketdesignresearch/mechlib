package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.DefaultAuctionRound;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.ElicitationEconomy;
import org.springframework.data.annotation.PersistenceConstructor;

import lombok.Getter;

public class MLQueryAuctionRound<T extends BundleValueBids<?>> extends DefaultAuctionRound<T> {

	@Getter
	private final T bids;
	@Getter
	private final Map<UUID, List<ElicitationEconomy>> marginalsToQueryNext;

	public MLQueryAuctionRound(Auction<T> auction, T bids, Map<UUID, List<ElicitationEconomy>> marginalsMap) {
		super(auction);
		this.bids = bids;
		this.marginalsToQueryNext = marginalsMap;
	}

	@PersistenceConstructor
	public MLQueryAuctionRound(int roundNumber, int auctionPhaseNumber, int auctionPhaseRoundNumber, T bids,
			Map<UUID, List<ElicitationEconomy>> marginalsMap) {
		super(roundNumber, auctionPhaseNumber, auctionPhaseRoundNumber);
		this.bids = bids;
		this.marginalsToQueryNext = marginalsMap;
	}
}
