package org.marketdesignresearch.mechlib.mechanisms.ccg.referencepoint;

import org.marketdesignresearch.mechlib.domain.Allocation;
import org.marketdesignresearch.mechlib.domain.AuctionInstance;
import org.marketdesignresearch.mechlib.domain.Payment;

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
