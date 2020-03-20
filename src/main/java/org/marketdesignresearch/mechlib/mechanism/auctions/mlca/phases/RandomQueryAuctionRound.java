package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.DefaultAuctionRound;
import org.springframework.data.annotation.PersistenceConstructor;

import lombok.Getter;

public class RandomQueryAuctionRound<T extends BundleValueBids<?>> extends DefaultAuctionRound<T>{

	@Getter
	private final T bids;
	
	public RandomQueryAuctionRound(Auction<T> auction, T bids) {
		super(auction);
		this.bids = bids;
	}
	
	@PersistenceConstructor
	public RandomQueryAuctionRound(int roundNumber, int auctionPhaseNumber, int auctionPhaseRoundNumber, T bids) {
		super(roundNumber,auctionPhaseNumber,auctionPhaseRoundNumber);
		this.bids = bids;
	}
}
