package org.marketdesignresearch.mechlib.mechanism.auctions.cca.supplementaryphase;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.DefaultAuctionRound;
import org.springframework.data.annotation.PersistenceConstructor;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class LastBidsValueQuerySupplementaryRound extends DefaultAuctionRound<BundleExactValueBids> {

	@Getter
	private final BundleExactValueBids bids;

	public LastBidsValueQuerySupplementaryRound(Auction<BundleExactValueBids> auction, BundleExactValueBids bids) {
		super(auction);
		this.bids = bids;
	}

	@PersistenceConstructor
	protected LastBidsValueQuerySupplementaryRound(int roundNumber, int auctionPhaseNumber, int auctionPhaseRoundNumber,
			BundleExactValueBids bids) {
		super(roundNumber, auctionPhaseNumber, auctionPhaseRoundNumber);
		this.bids = bids;
	}

	@Override
	public String getDescription() {
		return "Supplementary Round";
	}

}
