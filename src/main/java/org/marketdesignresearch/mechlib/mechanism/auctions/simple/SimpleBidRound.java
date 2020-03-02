package org.marketdesignresearch.mechlib.mechanism.auctions.simple;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.DefaultAuctionRound;
import org.springframework.data.annotation.PersistenceConstructor;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SimpleBidRound extends DefaultAuctionRound<BundleValuePair> {

	@Getter
	private final BundleValueBids<BundleValuePair> bids;
	
	public SimpleBidRound(Auction<BundleValuePair> auction, BundleValueBids<BundleValuePair> bids) {
		super(auction);
		this.bids = bids;
	}
	
	@PersistenceConstructor
	protected SimpleBidRound(int roundNumber, int auctionPhaseNumber, int auctionPhaseRoundNumber, BundleValueBids<BundleValuePair> bids) {
		super(roundNumber, auctionPhaseNumber, auctionPhaseRoundNumber);
		this.bids = bids;
	}
}
