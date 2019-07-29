package org.marketdesignresearch.mechlib.mechanisms.itemlevel.tiebreaker;

import org.marketdesignresearch.mechlib.core.bid.SingleItemBid;

public class AlphabeticTieBreaker implements TieBreaker {
    @Override
    public int compare(SingleItemBid o1, SingleItemBid o2) {
        return o1.getBidder().getName().compareTo(o2.getBidder().getName());
    }
}
