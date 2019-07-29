package org.marketdesignresearch.mechlib.auction;

import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.mechanisms.MechanismResult;

public interface AuctionRound {

    int getRoundNumber();
    Bids getBids();
    Prices getPrices();
    MechanismResult getMechanismResult();
    void setMechanismResult(MechanismResult mechanismResult);

    default String getDescription() {
        return "Auction round " + getRoundNumber();
    }

}
