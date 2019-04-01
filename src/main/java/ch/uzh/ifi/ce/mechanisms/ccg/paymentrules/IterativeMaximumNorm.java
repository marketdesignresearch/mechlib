package ch.uzh.ifi.ce.mechanisms.ccg.paymentrules;

import ch.uzh.ifi.ce.mechanisms.AuctionResult;
import ch.uzh.ifi.ce.domain.Bidder;
import ch.uzh.ifi.ce.domain.Payment;
import ch.uzh.ifi.ce.mechanisms.MetaInfo;
import com.google.common.math.DoubleMath;
import edu.harvard.econcs.jopt.solver.IMIP;
import edu.harvard.econcs.jopt.solver.IMIPResult;
import edu.harvard.econcs.jopt.solver.mip.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IterativeMaximumNorm extends PaymentNorm implements CorePaymentNorm {
    private final AuctionResult referencePoint;

    private class MaxDistanceConstraint {
        public MaxDistanceConstraint(Variable variable, Constraint upConstraint, Constraint downConstraint) {
            this.variable = variable;
            this.upConstraint = upConstraint;
            this.downConstraint = downConstraint;
        }

        private final Variable variable;
        private final Constraint upConstraint;
        private final Constraint downConstraint;

    }

    public IterativeMaximumNorm(AuctionResult referencePoint) {
        this.referencePoint = referencePoint;
    }

    @Override
    public Payment minimizeDistance(IMIP program) {
        IMIP newProgram = MIPWrapper.makeMIPWithoutObjective(program);

        List<MaxDistanceConstraint> maxConstraints = addNormObjective(newProgram);
        long totalSolveTime = 0;
        int totalLps = 0;
        IMIPResult programResult;
        do {
            programResult = solveProgram(newProgram);
            totalSolveTime += programResult.getSolveTime();
            ++totalLps;
            setProposedValues(programResult, newProgram);
            for (Iterator<MaxDistanceConstraint> it = maxConstraints.iterator(); it.hasNext();) {
                MaxDistanceConstraint maxDistanceConstraint = it.next();
                Constraint upConstraint = maxDistanceConstraint.upConstraint;
                Constraint downConstraint = maxDistanceConstraint.downConstraint;

                if (!(DoubleMath.fuzzyEquals(programResult.getDual(upConstraint), 0, 1e-6) && DoubleMath.fuzzyEquals(programResult.getDual(downConstraint), 0, 1e-6))) {
                    // fix payments maybe change to LEQ
                    Constraint holdPayments = new Constraint(CompareType.EQ, programResult.getValue(maxDistanceConstraint.variable));
                    holdPayments.addTerm(1, maxDistanceConstraint.variable);
                    newProgram.add(holdPayments);
                    newProgram.remove(upConstraint);
                    newProgram.remove(downConstraint);
                    it.remove();
                }
            }

        } while (!maxConstraints.isEmpty());
        setProposedValues(programResult, program);
        MetaInfo metaInfo = new MetaInfo();
        metaInfo.setLpSolveTime(totalSolveTime);
        metaInfo.setNumberOfLPs(totalLps);
        return adaptProgram(referencePoint.getWinners(), programResult, metaInfo);
    }

    @Override
    public List<MaxDistanceConstraint> addNormObjective(IMIP newProgram) {
        Variable maxDeviation = new Variable("max deviation", VarType.DOUBLE, -MIP.MAX_VALUE, MIP.MAX_VALUE);
        newProgram.add(maxDeviation);
        List<MaxDistanceConstraint> maxConstraints = new ArrayList<>(referencePoint.getWinners().size());
        for (Bidder bidder : referencePoint.getWinners()) {
            Variable bidderVariable = newProgram.getVar(BIDDER + bidder.getId());
            BigDecimal referencePayment = referencePoint.getPayment().paymentOf(bidder).getAmount();
            Constraint maxConstraintUp = new Constraint(CompareType.LEQ, referencePayment.doubleValue());
            maxConstraintUp.addTerm(1, bidderVariable);
            maxConstraintUp.addTerm(-1, maxDeviation);
            newProgram.add(maxConstraintUp);
            Constraint maxConstraintDown = new Constraint(CompareType.GEQ, referencePayment.doubleValue());
            maxConstraintDown.addTerm(1, bidderVariable);
            maxConstraintDown.addTerm(1, maxDeviation);
            newProgram.add(maxConstraintDown);
            maxConstraints.add(new MaxDistanceConstraint(bidderVariable, maxConstraintUp, maxConstraintDown));
        }
        newProgram.addObjectiveTerm(1, maxDeviation);
        return maxConstraints;
    }

    @Override
    public AuctionResult getReferencePoint() {
        return referencePoint;
    }

    @Override
    public PrimaryObjective getPrimaryObjectiveConstraint(IMIP mip) {
        throw new UnsupportedOperationException("Method returns unique Payment vector");
    }

}
