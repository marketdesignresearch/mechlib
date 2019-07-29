package org.marketdesignresearch.mechlib.auction.cca.bidcollection.supplementaryround;

import org.marketdesignresearch.mechlib.core.bid.Bid;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;

public interface SupplementaryRound {
    Bid getSupplementaryBids(String id, Bidder bidder);
    int getNumberOfSupplementaryBids();

    default String getDescription() {
        return "(no description provided)";
    }
}
