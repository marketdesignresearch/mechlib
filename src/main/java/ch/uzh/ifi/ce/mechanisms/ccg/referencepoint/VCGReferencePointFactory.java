package ch.uzh.ifi.ce.mechanisms.ccg.referencepoint;

import ch.uzh.ifi.ce.domain.Allocation;
import ch.uzh.ifi.ce.domain.AuctionInstance;
import ch.uzh.ifi.ce.domain.Payment;
import ch.uzh.ifi.ce.mechanisms.vcg.XORVCGAuction;

public class VCGReferencePointFactory implements ReferencePointFactory {

    @Override
    public Payment computeReferencePoint(AuctionInstance auctionInstance, Allocation allocation) {
        return new XORVCGAuction(auctionInstance).getPayment();
    }

    @Override
    public String getName() {
        return "VCG";
    }

    @Override
    public boolean belowCore() {
        return true;
    }
}
