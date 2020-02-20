package org.marketdesignresearch.mechlib.outcomerules.itemlevel.tiebreaker;

import org.marketdesignresearch.mechlib.core.bid.bundle.SingleItemBid;

public class AlphabeticTieBreaker implements TieBreaker {
    @Override
    public int compare(SingleItemBid o1, SingleItemBid o2) {
        return o1.getBidder().getName().compareTo(o2.getBidder().getName());
    }
}
