package ch.uzh.ifi.ce.mechanisms.ccg.referencepoint;

import ch.uzh.ifi.ce.domain.Allocation;
import ch.uzh.ifi.ce.domain.AuctionInstance;
import ch.uzh.ifi.ce.domain.Payment;

public interface ReferencePointFactory {

    Payment computeReferencePoint(AuctionInstance auctionInstance, Allocation allocation);

    String getName();

    /**
     * 
     * @return true if and only if the reference point is guaranteed to be below
     *         or at the border of the core
     */
    boolean belowCore();
}
