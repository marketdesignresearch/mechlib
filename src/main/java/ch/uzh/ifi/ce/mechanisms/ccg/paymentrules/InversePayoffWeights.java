package ch.uzh.ifi.ce.mechanisms.ccg.paymentrules;

import ch.uzh.ifi.ce.domain.AuctionResult;
import ch.uzh.ifi.ce.domain.Bidder;
import edu.harvard.econcs.jopt.solver.mip.MIP;

import java.math.BigDecimal;

public class InversePayoffWeights implements CorePaymentWeights {
    private final AuctionResult referencePoint;

    public InversePayoffWeights(AuctionResult referencePoint) {
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
