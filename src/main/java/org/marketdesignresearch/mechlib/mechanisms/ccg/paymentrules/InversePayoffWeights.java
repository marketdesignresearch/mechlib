package org.marketdesignresearch.mechlib.mechanisms.ccg.paymentrules;

import org.marketdesignresearch.mechlib.mechanisms.MechanismResult;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import edu.harvard.econcs.jopt.solver.mip.MIP;

import java.math.BigDecimal;

public class InversePayoffWeights implements CorePaymentWeights {
    private final MechanismResult referencePoint;

    public InversePayoffWeights(MechanismResult referencePoint) {
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
