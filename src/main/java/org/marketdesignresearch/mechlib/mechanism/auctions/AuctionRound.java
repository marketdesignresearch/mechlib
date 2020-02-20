package org.marketdesignresearch.mechlib.mechanism.auctions;

import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;

public interface AuctionRound<T extends BundleValuePair> {

    int getRoundNumber();
    BundleValueBids<T> getBids();
    Prices getPrices();
    Outcome getOutcome();
    void setOutcome(Outcome outcome);

    default String getDescription() {
        return "Auction round " + getRoundNumber();
    }

}
