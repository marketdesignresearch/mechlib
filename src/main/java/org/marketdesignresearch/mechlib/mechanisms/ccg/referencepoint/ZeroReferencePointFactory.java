package org.marketdesignresearch.mechlib.mechanisms.ccg.referencepoint;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.Payment;

public class ZeroReferencePointFactory implements ReferencePointFactory {


    @Override
    public Payment computeReferencePoint(Bids bids, Allocation allocation) {
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
