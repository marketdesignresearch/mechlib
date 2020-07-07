package org.marketdesignresearch.mechlib.mechanism.auctions.cca.supplementaryphase;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.DefaultPricedAuctionRound;
import org.springframework.data.annotation.PersistenceConstructor;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ProfitMaximizingSupplementaryRound extends DefaultPricedAuctionRound<BundleExactValueBids> {

	@Getter
	private final BundleExactValueBids bids;

	public ProfitMaximizingSupplementaryRound(Auction<BundleExactValueBids> auction, BundleExactValueBids bids,
			Prices prices) {
		super(auction, prices);
		this.bids = bids;
	}

	@PersistenceConstructor
	protected ProfitMaximizingSupplementaryRound(int roundNumber, int auctionPhaseNumber, int auctionPhaseRoundNumber,
			Prices prices, BundleExactValueBids bids) {
		super(roundNumber, auctionPhaseNumber, auctionPhaseRoundNumber, prices);
		this.bids = bids;
	}

	@Override
	public String getDescription() {
		return "Supplementary Profit Maximizing Round";
	}

	public String getType() {
		return "SUPPLEMENTARY PROFITMAX";
	}

}
