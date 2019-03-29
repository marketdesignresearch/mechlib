package ch.uzh.ifi.ce.mechanisms.ccg.paymentrules;

import ch.uzh.ifi.ce.domain.AuctionResult;
import ch.uzh.ifi.ce.mechanisms.ccg.blockingallocation.BlockedBidders;
import ch.uzh.ifi.ce.domain.Payment;
import ch.uzh.ifi.ce.mechanisms.MetaInfo;
import com.google.common.collect.Lists;
import edu.harvard.econcs.jopt.solver.IMIP;
import edu.harvard.econcs.jopt.solver.MIPException;
import edu.harvard.econcs.jopt.solver.mip.Constraint;
import edu.harvard.econcs.jopt.solver.mip.MIPWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ParameterizedCorePaymentRule extends BaseCorePaymentRule implements CorePaymentRule {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParameterizedCorePaymentRule.class);

    private final List<CorePaymentNorm> objectiveNorms;
    private final IMIP program;
    private MetaInfo metaInfo = new MetaInfo();
    private Payment result = null;

    public ParameterizedCorePaymentRule(CorePaymentNorm primaryObjectiveNorm, CorePaymentNorm... secondaryObjectiveNorms) {
        this(Lists.asList(primaryObjectiveNorm, secondaryObjectiveNorms));
    }

    public ParameterizedCorePaymentRule(List<CorePaymentNorm> objectiveNorms) {
        AuctionResult referencePoint = objectiveNorms.get(0).getReferencePoint();
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
                result = objectiveNorms.get(objectiveNorms.size() - 1).minimizeDistance(programCopy);
                metaInfo = metaInfo.join(result.getMetaInfo());
            } catch (MIPException ex) {
                LOGGER.warn("Failed to compute Payments. Base Program: {}", program);
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
