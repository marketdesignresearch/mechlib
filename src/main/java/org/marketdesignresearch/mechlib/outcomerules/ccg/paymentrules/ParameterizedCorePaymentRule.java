package org.marketdesignresearch.mechlib.outcomerules.ccg.paymentrules;

import com.google.common.collect.Lists;
import edu.harvard.econcs.jopt.solver.IMIP;
import edu.harvard.econcs.jopt.solver.MIPException;
import edu.harvard.econcs.jopt.solver.mip.Constraint;
import edu.harvard.econcs.jopt.solver.mip.MIPWrapper;
import lombok.extern.slf4j.Slf4j;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.Payment;
import org.marketdesignresearch.mechlib.metainfo.MetaInfo;
import org.marketdesignresearch.mechlib.outcomerules.ccg.blockingallocation.BlockedBidders;

import java.util.List;

@Slf4j
public class ParameterizedCorePaymentRule extends BaseCorePaymentRule implements CorePaymentRule {

    private final List<CorePaymentNorm> objectiveNorms;
    private final IMIP program;
    private MetaInfo metaInfo = new MetaInfo();
    private Payment result;

    public ParameterizedCorePaymentRule(CorePaymentNorm primaryObjectiveNorm, CorePaymentNorm... secondaryObjectiveNorms) {
        this(Lists.asList(primaryObjectiveNorm, secondaryObjectiveNorms));
    }

    public ParameterizedCorePaymentRule(List<CorePaymentNorm> objectiveNorms) {
        Outcome referencePoint = objectiveNorms.get(0).getReferencePoint();
        this.objectiveNorms = objectiveNorms;
        this.result = referencePoint.getPayment();
        program = createProgram(referencePoint.getAllocation(), referencePoint.getPayment());
    }

    @Override
    public Payment getPayment() {
        if (result == null) {
            try {

                IMIP programCopy = MIPWrapper.makeMIPWithoutObjective(program);
                for (int i = 0; i < objectiveNorms.size() - 1; ++i) {
                    PrimaryObjective primaryObjective = objectiveNorms.get(i).getPrimaryObjectiveConstraint(programCopy);
                    for (Constraint constraint : primaryObjective.getConstraints()) {
                        constraint.setConstant(constraint.getConstant());
                        programCopy.add(constraint);
                    }
                    metaInfo = metaInfo.join(primaryObjective.getMetaInfo());
                }
                CorePaymentNorm norm = objectiveNorms.get(objectiveNorms.size() - 1);
                norm.setMipInstrumentation(getMipInstrumentation());
                result = norm.minimizeDistance(programCopy);
                metaInfo = metaInfo.join(result.getMetaInfo());
            } catch (MIPException ex) {
                log.warn("Failed to compute Payments. Base Program: {}", program);
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
