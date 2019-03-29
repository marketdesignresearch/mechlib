package ch.uzh.ifi.ce.mechanisms.ccg.paymentrules;

import ch.uzh.ifi.ce.domain.Allocation;
import ch.uzh.ifi.ce.domain.AuctionResult;
import ch.uzh.ifi.ce.domain.Payment;
import ch.uzh.ifi.ce.mechanisms.MetaInfo;
import ch.uzh.ifi.ce.mechanisms.ccg.blockingallocation.BlockedBidders;
import ch.uzh.ifi.ce.utils.CPLEXUtils;
import edu.harvard.econcs.jopt.solver.IMIP;
import edu.harvard.econcs.jopt.solver.IMIPResult;
import edu.harvard.econcs.jopt.solver.MIPException;
import edu.harvard.econcs.jopt.solver.mip.Constraint;
import edu.harvard.econcs.jopt.solver.mip.LinearTerm;
import edu.harvard.econcs.jopt.solver.mip.MIPWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates a payment norm that is the sum of all the provided norms The program
 * will be adapted using the primary norm This can for example be used to create
 * a secondary objective by setting different weights for each norm
 * 
 * @author Benedikt
 *
 */
public class MultiNormCorePaymentRule extends BaseCorePaymentRule implements CorePaymentRule {
    private static final Logger LOGGER = LoggerFactory.getLogger(MultiNormCorePaymentRule.class);
    private final PaymentNorm primaryNorm;
    private final PaymentNorm[] additionalNorms;
    private final IMIP program;
    private MetaInfo metaInfo = new MetaInfo();
    private Payment result = null;
    private final Allocation allocation;

    public MultiNormCorePaymentRule(AuctionResult referencePoint, PaymentNorm primaryNorm, PaymentNorm... additionalNorms) {
        this.primaryNorm = primaryNorm;
        this.additionalNorms = additionalNorms;
        this.result = referencePoint.getPayment();
        this.allocation = referencePoint.getAllocation();
        program = createProgram(referencePoint.getAllocation(), result);
    }

    @Override
    public Payment getPayment() {
        if (result == null) {
            try {
                IMIP tempProgram = MIPWrapper.makeMIPWithoutObjective(program);
                primaryNorm.addNormObjective(tempProgram);
                for (PaymentNorm paymentNorm : additionalNorms) {
                    paymentNorm.addNormObjective(tempProgram);
                }
                IMIPResult mipResult = CPLEXUtils.SOLVER.solve(tempProgram);
                MetaInfo tempMetaInfo = new MetaInfo();
                tempMetaInfo.setNumberOfQPs(1);
                result = primaryNorm.adaptProgram(allocation.getWinners(), mipResult, tempMetaInfo);
                metaInfo = metaInfo.join(result.getMetaInfo());
            } catch (MIPException ex) {
                for (Constraint constraint : program.getConstraints()) {
                    double sumOfUpperBounds = 0;
                    for (LinearTerm term : constraint.getLinearTerms()) {
                        sumOfUpperBounds += program.getVar(term.getVarName()).getUpperBound();
                    }
                    LOGGER.debug("Upper Bounds " + sumOfUpperBounds + " Constraint " + constraint.getConstant());
                    if (sumOfUpperBounds - constraint.getConstant() < 1e-4) {
                        LOGGER.error("Constraint is invalid {}", constraint);
                    }
                }
                LOGGER.error("BPO infeasible", ex);
                throw new MIPException("BPO infeasible", ex);
            }

        }
        return new Payment(result.getPaymentMap(), metaInfo);
    }

    @Override
    public void resetResult() {
        result = null;
    }

    @Override
    public void addBlockingConstraint(BlockedBidders blockedBidders, Payment lastPayment) {
        MetaInfo newMetaInfo = addBlockingConstraint(program, blockedBidders, lastPayment);
        metaInfo = metaInfo.join(newMetaInfo);
    }

}
