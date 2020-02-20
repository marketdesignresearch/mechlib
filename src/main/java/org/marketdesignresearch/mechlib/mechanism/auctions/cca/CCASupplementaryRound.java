package org.marketdesignresearch.mechlib.mechanism.auctions.cca;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.mechanism.auctions.DefaultAuctionRound;
import org.springframework.data.annotation.PersistenceConstructor;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CCASupplementaryRound extends DefaultAuctionRound<BundleValuePair> {

    @PersistenceConstructor
    public CCASupplementaryRound(int roundNumber, BundleValueBids<BundleValuePair> bids, Prices prices) {
        super(roundNumber, bids, prices);
    }

    @Override
    public String getDescription() {
        return "Supplementary Round";
    }

    public String getType() {
        return "SUPPLEMENTARY";
    }


}

