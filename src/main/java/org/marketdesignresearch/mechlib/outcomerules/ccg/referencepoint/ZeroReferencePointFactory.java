package org.marketdesignresearch.mechlib.outcomerules.ccg.referencepoint;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.Payment;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;

public class ZeroReferencePointFactory implements ReferencePointFactory {


    @Override
    public Payment computeReferencePoint(BundleValueBids<?> bids, Allocation allocation) {
        return Payment.ZERO;
    }

    @Override
    public String getName() {
        return "ZERO";
    }

    @Override
    public boolean belowCore() {
        return true;
    }

}
