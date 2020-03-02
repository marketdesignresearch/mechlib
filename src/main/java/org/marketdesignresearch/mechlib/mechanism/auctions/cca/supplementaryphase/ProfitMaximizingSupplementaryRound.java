package org.marketdesignresearch.mechlib.mechanism.auctions.cca.supplementaryphase;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.DefaultAuctionRound;
import org.marketdesignresearch.mechlib.mechanism.auctions.DefaultPricedAuctionRound;
import org.springframework.data.annotation.PersistenceConstructor;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ProfitMaximizingSupplementaryRound extends DefaultPricedAuctionRound<BundleValuePair> {

	@Getter
	private final BundleValueBids<BundleValuePair> bids;
	
    public ProfitMaximizingSupplementaryRound(Auction<BundleValuePair> auction, BundleValueBids<BundleValuePair> bids, Prices prices) {
        super(auction, prices);
        this.bids = bids;
    }
    
    @PersistenceConstructor
    protected ProfitMaximizingSupplementaryRound(int roundNumber, int auctionPhaseNumber, int auctionPhaseRoundNumber, Prices prices, BundleValueBids<BundleValuePair> bids) {
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

