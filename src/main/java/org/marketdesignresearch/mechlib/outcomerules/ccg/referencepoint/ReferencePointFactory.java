package org.marketdesignresearch.mechlib.outcomerules.ccg.referencepoint;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.Payment;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;

public interface ReferencePointFactory {

    Payment computeReferencePoint(BundleValueBids<? extends BundleValuePair> bids, Allocation allocation);

    String getName();

    /**
     * 
     * @return true if and only if the reference point is guaranteed to be below
     *         or at the border of the core
     */
    boolean belowCore();
}
