package org.marketdesignresearch.mechlib.mechanism.auctions;

import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.core.Outcome;

public interface AuctionRound {

    int getRoundNumber();
    Bids getBids();
    Prices getPrices();
    Outcome getOutcome();
    void setOutcome(Outcome outcome);

    default String getDescription() {
        return "Auction round " + getRoundNumber();
    }

}
