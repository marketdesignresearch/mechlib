package ch.uzh.ifi.ce.mechanisms.ccg.paymentrules;

import ch.uzh.ifi.ce.domain.AuctionResult;
import ch.uzh.ifi.ce.domain.Bidder;
import ch.uzh.ifi.ce.domain.Payment;
import ch.uzh.ifi.ce.mechanisms.MetaInfo;
import edu.harvard.econcs.jopt.solver.IMIP;
import edu.harvard.econcs.jopt.solver.IMIPResult;
import edu.harvard.econcs.jopt.solver.mip.CompareType;
import edu.harvard.econcs.jopt.solver.mip.Constraint;
import edu.harvard.econcs.jopt.solver.mip.MIPWrapper;
import edu.harvard.econcs.jopt.solver.mip.Variable;

import java.math.BigDecimal;

public class EuclideanNorm extends PaymentNorm implements CorePaymentNorm {
    private final AuctionResult referencePoint;
    private final CorePaymentWeights weights;

    public EuclideanNorm(AuctionResult referencePoint, CorePaymentWeights weights) {
        this.referencePoint = referencePoint;
        this.weights = weights;
    }

    @Override
    public Payment minimizeDistance(IMIP program) {
        IMIP newProgram = MIPWrapper.makeMIPWithoutObjective(program);
        addNormObjective(newProgram);
        IMIPResult result = solveProgram(newProgram);
        setProposedValues(result, program);
        MetaInfo metaInfo = new MetaInfo();
        metaInfo.setNumberOfQPs(1);
        metaInfo.setQpSolveTime(result.getSolveTime());
        return adaptProgram(referencePoint.getWinners(), result, metaInfo);
    }

    @Override
    public Void addNormObjective(IMIP mip) {
        for (Bidder winner : referencePoint.getWinners()) {
            double weight = weights.getWeight(winner);
            BigDecimal referencePayment = referencePoint.getPayment().paymentOf(winner).getAmount();
            Variable variable = mip.getVar(BIDDER + winner.getId());
            mip.addObjectiveTerm(weight, variable, variable);
            mip.addObjectiveTerm(-2d * weight * referencePayment.doubleValue(), variable);
        }
        return null;
    }

    @Override
    public AuctionResult getReferencePoint() {
        return referencePoint;
    }

    @Override
    public PrimaryObjective getPrimaryObjectiveConstraint(IMIP program) {
        MetaInfo metaInfo = new MetaInfo();

        IMIP newProgram = MIPWrapper.makeMIPWithoutObjective(program);
        addNormObjective(newProgram);
        IMIPResult result = solveProgram(newProgram);
        metaInfo.setLpSolveTime(result.getSolveTime());
        metaInfo.setNumberOfQPs(1);
        setProposedValues(result, program);
        double objectiveValue = result.getObjectiveValue();
     
        Constraint primaryObjectiveConstraint = new Constraint(CompareType.LEQ, objectiveValue);
        newProgram.getQuadraticObjectiveTerms().forEach(primaryObjectiveConstraint::addTerm);
        newProgram.getLinearObjectiveTerms().forEach(primaryObjectiveConstraint::addTerm);
        return new PrimaryObjective(primaryObjectiveConstraint, metaInfo);
    }

}
