package org.marketdesignresearch.mechlib.mechanisms.ccg.referencepoint;

import org.marketdesignresearch.mechlib.domain.Allocation;
import org.marketdesignresearch.mechlib.domain.AuctionInstance;
import org.marketdesignresearch.mechlib.domain.Payment;

public class CachedReferencePointFactory implements ReferencePointFactory {
    private Payment payment;

    public CachedReferencePointFactory(Payment payment) {
        this.payment = payment;
    }


    @Override
    public Payment computeReferencePoint(AuctionInstance auctionInstance, Allocation allocation) {
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
