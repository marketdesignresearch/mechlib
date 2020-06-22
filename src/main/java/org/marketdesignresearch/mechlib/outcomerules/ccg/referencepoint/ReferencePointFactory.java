package org.marketdesignresearch.mechlib.outcomerules.ccg.referencepoint;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.Payment;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;

public interface ReferencePointFactory {

    Payment computeReferencePoint(BundleValueBids<?> bids, Allocation allocation);

    String getName();

    /**
     * 
     * @return true if and only if the reference point is guaranteed to be below
     *         or at the border of the core
     */
    boolean belowCore();
}
