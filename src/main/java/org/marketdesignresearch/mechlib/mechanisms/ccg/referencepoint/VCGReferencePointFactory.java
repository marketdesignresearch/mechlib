package org.marketdesignresearch.mechlib.mechanisms.ccg.referencepoint;

import org.marketdesignresearch.mechlib.domain.Allocation;
import org.marketdesignresearch.mechlib.domain.AuctionInstance;
import org.marketdesignresearch.mechlib.domain.Payment;
import org.marketdesignresearch.mechlib.mechanisms.vcg.XORVCGAuction;

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
