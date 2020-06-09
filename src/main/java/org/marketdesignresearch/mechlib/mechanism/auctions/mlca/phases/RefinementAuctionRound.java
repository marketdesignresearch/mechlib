package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases;

import java.util.Set;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBids;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.DefaultPricedAuctionRound;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.RefinementType;

import lombok.Getter;

public class RefinementAuctionRound extends DefaultPricedAuctionRound<BundleBoundValueBids>{

	@Getter
	private final BundleBoundValueBids bids;
	@Getter
	private final Set<RefinementType> refinements;
	@Getter
	private final Allocation alphaAllocation;
	
	public RefinementAuctionRound(Auction<BundleBoundValueBids> auction, Prices prices, BundleBoundValueBids bids, Set<RefinementType> refinements, Allocation alphaAllocation) {
		super(auction, prices);
		this.bids = bids;
		this.refinements = refinements;
		this.alphaAllocation = alphaAllocation;
	}

}
