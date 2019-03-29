package ch.uzh.ifi.ce.mechanisms.ccg.paymentrules;

import ch.uzh.ifi.ce.domain.Allocation;
import ch.uzh.ifi.ce.domain.Bidder;
import ch.uzh.ifi.ce.mechanisms.ccg.blockingallocation.BlockedBidders;
import ch.uzh.ifi.ce.domain.Payment;
import ch.uzh.ifi.ce.mechanisms.MetaInfo;
import ch.uzh.ifi.ce.utils.PrecisionUtils;
import com.google.common.math.DoubleMath;
import edu.harvard.econcs.jopt.solver.IMIP;
import edu.harvard.econcs.jopt.solver.mip.*;

import java.math.BigDecimal;

public abstract class BaseCorePaymentRule implements CorePaymentRule {

    protected IMIP createProgram(Allocation allocation, Payment referencePoint) {
        IMIP program = MIPWrapper.makeNewMinMIP();

        for (Bidder winner : allocation.getWinners()) {
            BigDecimal lowerBound = referencePoint.paymentOf(winner).getAmount();
            BigDecimal upperBound = allocation.allocationOf(winner).getValue();
            if (lowerBound.compareTo(upperBound) > 0) {
                lowerBound = upperBound;
            }
            Variable winnerPayment = new Variable(PaymentNorm.BIDDER + winner.getId(), VarType.DOUBLE, lowerBound.doubleValue(), upperBound.doubleValue());
            program.add(winnerPayment);
        }
        return program;
    }

    protected MetaInfo addBlockingConstraint(IMIP program, BlockedBidders blockedBidders, Payment lastPayment) {
        Constraint blockingConstraint = new Constraint(CompareType.GEQ, blockedBidders.getBlockedAmount(lastPayment).doubleValue());
        double vcgPayments = 0;
        for (Bidder blockedBidder : blockedBidders.getNonTraitors()) {
            Variable blockedWinnerPayment = program.getVar(PaymentNorm.BIDDER + blockedBidder.getId());
            if (blockedWinnerPayment == null) {
                throw new IllegalArgumentException("Variable " + PaymentNorm.BIDDER + blockedBidder.getId() + " does not exist");
            }
            blockingConstraint.addTerm(1, blockedWinnerPayment);
            vcgPayments += blockedWinnerPayment.getLowerBound();
        }
        MetaInfo newMetaInfo = new MetaInfo();
        // Check whether the constraint has any bite.
        if (blockingConstraint.size() > 0 && DoubleMath.fuzzyCompare(blockingConstraint.getConstant(), vcgPayments, PrecisionUtils.EPSILON.doubleValue()) > 0) {
            program.add(blockingConstraint);
            newMetaInfo.setConstraintsGenerated(1);

        } else {

            newMetaInfo.setIgnoredConstraints(1);
        }
        return newMetaInfo;
    }

}