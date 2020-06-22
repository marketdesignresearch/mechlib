package org.marketdesignresearch.mechlib.outcomerules.ccg.paymentrules;

import java.math.BigDecimal;

import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;

import edu.harvard.econcs.jopt.solver.mip.MIP;

public class InversePayoffWeights implements CorePaymentWeights {
    private final Outcome referencePoint;

    public InversePayoffWeights(Outcome referencePoint) {
        this.referencePoint = referencePoint;

    }

    @Override
    public double getWeight(Bidder bidder) {
        double bidderPayment = referencePoint.payoffOf(bidder).doubleValue();
        if (bidderPayment <= 0) {
            BigDecimal totalPayoff = referencePoint.getAllocation().getTotalAllocationValue().subtract(referencePoint.getPayment().getTotalPayments());
            return totalPayoff.signum()==0 ? 1d : MIP.MAX_VALUE;
        } else {
            return 1d / bidderPayment;
        }
    }

}
