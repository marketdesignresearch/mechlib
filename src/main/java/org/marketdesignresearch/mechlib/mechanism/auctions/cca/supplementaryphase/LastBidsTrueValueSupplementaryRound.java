package org.marketdesignresearch.mechlib.mechanism.auctions.cca.supplementaryphase;

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
public class LastBidsTrueValueSupplementaryRound extends DefaultAuctionRound<BundleValuePair> {

	@Getter
	private final BundleValueBids<BundleValuePair> bids;
	
    @PersistenceConstructor
    public LastBidsTrueValueSupplementaryRound(Auction<BundleValuePair> auction, BundleValueBids<BundleValuePair> bids) {
        super(auction);
        this.bids = bids;
    }

    @Override
    public String getDescription() {
        return "Supplementary Round";
    }

    public String getType() {
        return "SUPPLEMENTARY";
    }


}

