package ch.uzh.ifi.ce.mechanisms.ccg.referencepoint;

import ch.uzh.ifi.ce.domain.Allocation;
import ch.uzh.ifi.ce.domain.AuctionInstance;
import ch.uzh.ifi.ce.domain.Payment;

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
