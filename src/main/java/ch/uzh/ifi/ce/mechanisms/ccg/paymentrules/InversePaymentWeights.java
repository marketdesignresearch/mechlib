package ch.uzh.ifi.ce.mechanisms.ccg.paymentrules;

import ch.uzh.ifi.ce.domain.Allocation;
import ch.uzh.ifi.ce.domain.Bidder;
import ch.uzh.ifi.ce.domain.Payment;
import edu.harvard.econcs.jopt.solver.mip.MIP;

public class InversePaymentWeights implements CorePaymentWeights {
    private final Payment payment;

    public InversePaymentWeights(Allocation allocation, Payment payment) {
        this.payment = payment;
    }

    @Override
    public double getWeight(Bidder bidder) {
        double bidderPayment = payment.paymentOf(bidder).getAmount().doubleValue();
        if (bidderPayment <= 0) {

            return payment.getTotalPayments().signum()==0?1d: MIP.MAX_VALUE;
        } else {
            return 1d / bidderPayment;
        }
    }

}
