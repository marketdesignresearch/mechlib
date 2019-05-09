package org.marketdesignresearch.mechlib.mechanisms.ccg.referencepoint;

import org.marketdesignresearch.mechlib.domain.Allocation;
import org.marketdesignresearch.mechlib.domain.bid.Bids;
import org.marketdesignresearch.mechlib.domain.Payment;

public interface ReferencePointFactory {

    Payment computeReferencePoint(Bids bids, Allocation allocation);

    String getName();

    /**
     * 
     * @return true if and only if the reference point is guaranteed to be below
     *         or at the border of the core
     */
    boolean belowCore();
}