package org.marketdesignresearch.mechlib.mechanism.auctions.cca.bidcollection.supplementaryround;

import org.marketdesignresearch.mechlib.core.bid.Bid;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.price.Prices;

public interface SupplementaryRound {
    Bid getSupplementaryBids(String id, Bidder bidder, Prices prices);
    int getNumberOfSupplementaryBids();

    default String getDescription() {
        return "(no description provided)";
    }
}
