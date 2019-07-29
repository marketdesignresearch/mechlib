package org.marketdesignresearch.mechlib.mechanisms.ccg.referencepoint;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.Payment;
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
