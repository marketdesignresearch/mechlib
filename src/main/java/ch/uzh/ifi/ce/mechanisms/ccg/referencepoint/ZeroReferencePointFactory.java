package ch.uzh.ifi.ce.mechanisms.ccg.referencepoint;

import ch.uzh.ifi.ce.domain.Allocation;
import ch.uzh.ifi.ce.domain.AuctionInstance;
import ch.uzh.ifi.ce.domain.Payment;

public class ZeroReferencePointFactory implements ReferencePointFactory {


    @Override
    public Payment computeReferencePoint(AuctionInstance auctionInstance, Allocation allocation) {
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
