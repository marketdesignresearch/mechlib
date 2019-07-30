package org.marketdesignresearch.mechlib.outcomerules.ccg.referencepoint;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.Payment;
import org.marketdesignresearch.mechlib.outcomerules.vcg.XORVCGRule;

public class VCGReferencePointFactory implements ReferencePointFactory {

    @Override
    public Payment computeReferencePoint(Bids bids, Allocation allocation) {
        return new XORVCGRule(bids).getPayment();
    }

    @Override
    public String getName() {
        return "VCG Reference Point";
    }

    @Override
    public boolean belowCore() {
        return true;
    }
}
