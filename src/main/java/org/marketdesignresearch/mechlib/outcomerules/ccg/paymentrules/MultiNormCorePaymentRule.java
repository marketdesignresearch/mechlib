package org.marketdesignresearch.mechlib.outcomerules.ccg.paymentrules;

import edu.harvard.econcs.jopt.solver.IMIP;
import edu.harvard.econcs.jopt.solver.IMIPResult;
import edu.harvard.econcs.jopt.solver.MIPException;
import edu.harvard.econcs.jopt.solver.mip.Constraint;
import edu.harvard.econcs.jopt.solver.mip.LinearTerm;
import edu.harvard.econcs.jopt.solver.mip.MIPWrapper;
import lombok.extern.slf4j.Slf4j;
import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.Payment;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentation;
import org.marketdesignresearch.mechlib.metainfo.MetaInfo;
import org.marketdesignresearch.mechlib.outcomerules.ccg.blockingallocation.BlockedBidders;
import org.marketdesignresearch.mechlib.utils.CPLEXUtils;

/**
 * Creates a payment norm that is the sum of all the provided norms The program
 * will be adapted using the primary norm This can for example be used to create
 * a secondary objective by setting different weights for each norm
 * 
 * @author Benedikt
 *
 */
@Slf4j
public class MultiNormCorePaymentRule extends BaseCorePaymentRule implements CorePaymentRule {
    private final PaymentNorm primaryNorm;
    private final PaymentNorm[] additionalNorms;
    private final IMIP program;
    private MetaInfo metaInfo = new MetaInfo();
    private Payment result;
    private final Allocation allocation;

    public MultiNormCorePaymentRule(Outcome referencePoint, PaymentNorm primaryNorm, PaymentNorm... additionalNorms) {
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
                getMipInstrumentation().postMIP(MipInstrumentation.MipPurpose.PAYMENT, tempProgram, mipResult);
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
                    log.debug("Upper Bounds " + sumOfUpperBounds + " Constraint " + constraint.getConstant());
                    if (sumOfUpperBounds - constraint.getConstant() < 1e-4) {
                        log.error("Constraint is invalid {}", constraint);
                    }
                }
                log.error("BPO infeasible", ex);
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
