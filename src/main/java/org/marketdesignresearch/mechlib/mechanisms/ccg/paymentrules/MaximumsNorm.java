package org.marketdesignresearch.mechlib.mechanisms.ccg.paymentrules;

import org.marketdesignresearch.mechlib.mechanisms.AuctionResult;
import org.marketdesignresearch.mechlib.domain.Bidder;
import org.marketdesignresearch.mechlib.domain.Payment;
import org.marketdesignresearch.mechlib.mechanisms.MetaInfo;
import edu.harvard.econcs.jopt.solver.IMIP;
import edu.harvard.econcs.jopt.solver.IMIPResult;
import edu.harvard.econcs.jopt.solver.mip.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MaximumsNorm extends PaymentNorm implements CorePaymentNorm {
    private final AuctionResult referencePoint;

    public MaximumsNorm(AuctionResult referencePoint) {
        this.referencePoint = referencePoint;
    }

    @Override
    public Payment minimizeDistance(IMIP program) {
        IMIP newProgram = MIPWrapper.makeMIPWithoutObjective(program);
        addNormObjective(newProgram);
        IMIPResult result = solveProgram(newProgram);
        setProposedValues(result, program);
        MetaInfo metaInfo = new MetaInfo();
        metaInfo.setNumberOfLPs(1);
        metaInfo.setLpSolveTime(result.getSolveTime());

        return adaptProgram(referencePoint.getWinners(), result, metaInfo);
    }

    @Override
    public Void addNormObjective(IMIP program) {
        Variable maxDeviation = new Variable("max deviation", VarType.DOUBLE, 0, MIP.MAX_VALUE);
        program.add(maxDeviation);
        for (Bidder bidder : referencePoint.getWinners()) {
            Variable bidderVariable = program.getVar(BIDDER + bidder.getId());
            BigDecimal referencePayment = referencePoint.getPayment().paymentOf(bidder).getAmount();
            Constraint maxConstraintUp = new Constraint(CompareType.LEQ, referencePayment.doubleValue());
            maxConstraintUp.addTerm(1, bidderVariable);
            maxConstraintUp.addTerm(-1, maxDeviation);
            program.add(maxConstraintUp);
            Constraint maxConstraintDown = new Constraint(CompareType.GEQ, referencePayment.doubleValue());
            maxConstraintDown.addTerm(1, bidderVariable);
            maxConstraintDown.addTerm(1, maxDeviation);
            program.add(maxConstraintDown);

        }
        program.addObjectiveTerm(1, maxDeviation);
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
        metaInfo.setNumberOfLPs(1);
        setProposedValues(result, program);
        double objectiveValue = result.getObjectiveValue();
        List<Constraint> constraints = new ArrayList<>(referencePoint.getWinners().size());
        for (Bidder bidder : referencePoint.getWinners()) {
            BigDecimal referencePayment = referencePoint.getPayment().paymentOf(bidder).getAmount();
            Variable bidderVariable = program.getVar(BIDDER + bidder.getId());
            Constraint maxConstraintUp = new Constraint(CompareType.LEQ, referencePayment.doubleValue() + objectiveValue);
            maxConstraintUp.addTerm(1, bidderVariable);
            program.add(maxConstraintUp);
            Constraint maxConstraintDown = new Constraint(CompareType.GEQ, referencePayment.doubleValue() - objectiveValue);
            maxConstraintDown.addTerm(1, bidderVariable);
        }
        return new PrimaryObjective(constraints, metaInfo);
    }

}
