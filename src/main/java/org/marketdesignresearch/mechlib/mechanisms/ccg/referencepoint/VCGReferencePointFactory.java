package org.marketdesignresearch.mechlib.mechanisms.ccg.referencepoint;

import org.marketdesignresearch.mechlib.domain.Allocation;
import org.marketdesignresearch.mechlib.domain.Bids;
import org.marketdesignresearch.mechlib.domain.Payment;
import org.marketdesignresearch.mechlib.mechanisms.vcg.XORVCGAuction;

public class VCGReferencePointFactory implements ReferencePointFactory {

    @Override
    public Payment computeReferencePoint(Bids bids, Allocation allocation) {
        return new XORVCGAuction(bids).getPayment();
    }

    @Override
    public String getName() {
        return "Mechanism";
    }

    @Override
    public boolean belowCore() {
        return true;
    }
}
