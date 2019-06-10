package org.marketdesignresearch.mechlib.mechanisms.ccg.referencepoint;

import org.marketdesignresearch.mechlib.domain.Allocation;
import org.marketdesignresearch.mechlib.domain.bid.Bids;
import org.marketdesignresearch.mechlib.domain.Payment;
import org.marketdesignresearch.mechlib.mechanisms.vcg.XORVCGMechanism;

public class VCGReferencePointFactory implements ReferencePointFactory {

    @Override
    public Payment computeReferencePoint(Bids bids, Allocation allocation) {
        return new XORVCGMechanism(bids).getPayment();
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
