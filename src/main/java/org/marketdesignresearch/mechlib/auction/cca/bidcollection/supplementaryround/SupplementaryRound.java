package org.marketdesignresearch.mechlib.auction.cca.bidcollection.supplementaryround;

import org.marketdesignresearch.mechlib.domain.bid.Bid;
import org.marketdesignresearch.mechlib.domain.bidder.Bidder;

public interface SupplementaryRound {
    Bid getSupplementaryBids(String id, Bidder bidder);

    default String getDescription() {
        return "(no description provided)";
    }
}
