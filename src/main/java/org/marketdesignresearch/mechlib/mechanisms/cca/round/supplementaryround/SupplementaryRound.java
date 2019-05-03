package org.marketdesignresearch.mechlib.mechanisms.cca.round.supplementaryround;

import org.marketdesignresearch.mechlib.domain.Bid;
import org.marketdesignresearch.mechlib.domain.Bidder;

public interface SupplementaryRound {
    Bid getSupplementaryBids(String id, Bidder bidder);

    default String getDescription() {
        return "(no description provided)";
    }
}
