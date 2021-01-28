package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases;

import java.math.BigDecimal;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBids;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.DefaultAuctionRound;

import lombok.Getter;

public class ConvergenceAuctionRound extends DefaultAuctionRound<BundleBoundValueBids>{

	@Getter
	private final BundleBoundValueBids bids;
	@Getter
	private final BigDecimal epsilon;
	
	public ConvergenceAuctionRound(Auction<BundleBoundValueBids> auction, BundleBoundValueBids bids, BigDecimal epsilon) {
		super(auction);
		this.bids = bids;
		this.epsilon = epsilon;
	}
}
