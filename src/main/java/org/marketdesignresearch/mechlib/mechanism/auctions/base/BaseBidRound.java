package org.marketdesignresearch.mechlib.mechanism.auctions.base;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.DefaultAuctionRound;
import org.springframework.data.annotation.PersistenceConstructor;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class BaseBidRound extends DefaultAuctionRound<BundleExactValueBids> {

	@Getter
	private final BundleExactValueBids bids;
	
	public BaseBidRound(Auction<BundleExactValueBids> auction, BundleExactValueBids bids) {
		super(auction);
		this.bids = bids;
	}
	
	@PersistenceConstructor
	protected BaseBidRound(int roundNumber, int auctionPhaseNumber, int auctionPhaseRoundNumber, BundleExactValueBids bids) {
		super(roundNumber, auctionPhaseNumber, auctionPhaseRoundNumber);
		this.bids = bids;
	}
}
