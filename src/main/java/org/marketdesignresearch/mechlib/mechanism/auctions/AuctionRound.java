package org.marketdesignresearch.mechlib.mechanism.auctions;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;

public interface AuctionRound<T extends BundleValuePair> {

    int getRoundNumber();
    int getAuctionPhaseNumber();
    int getAuctionPhaseRoundNumber();
    BundleValueBids<T> getBids();

    default String getDescription() {
        return "Auction round " + getRoundNumber();
    }
}
