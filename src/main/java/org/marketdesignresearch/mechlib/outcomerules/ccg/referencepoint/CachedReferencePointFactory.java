package org.marketdesignresearch.mechlib.outcomerules.ccg.referencepoint;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.Payment;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;

public class CachedReferencePointFactory implements ReferencePointFactory {
    private Payment payment;

    public CachedReferencePointFactory(Payment payment) {
        this.payment = payment;
    }


    @Override
    public Payment computeReferencePoint(BundleValueBids<?> bids, Allocation allocation) {
        return payment;
    }

    @Override
    public String getName() {
        return "CACHED";
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    @Override
    public boolean belowCore() {
        return false;
    }

}
