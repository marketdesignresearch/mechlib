package org.marketdesignresearch.mechlib.winnerdetermination;

import edu.harvard.econcs.jopt.solver.*;
import edu.harvard.econcs.jopt.solver.client.SolverClient;
import edu.harvard.econcs.jopt.solver.mip.MIP;
import edu.harvard.econcs.jopt.solver.mip.PoolSolution;
import org.marketdesignresearch.mechlib.domain.Allocation;
import org.marketdesignresearch.mechlib.mechanisms.AllocationRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class WinnerDetermination implements AllocationRule {
    private static final Logger LOGGER = LoggerFactory.getLogger(WinnerDetermination.class);
    private Allocation result = null;
    private List<Allocation> intermediateSolutions = null;
    private double lowerBound = -MIP.MAX_VALUE;
    private double epsilon = 1e-6;
    private boolean displayOutput = false;

    protected abstract IMIP getMIP();

    @Override
    public Allocation getAllocation() {
        if (result == null) {
            result = solveWinnerDetermination();
        }
        return result;
    }

    public abstract List<Allocation> getBestAllocations(int k);

    protected Allocation solveWinnerDetermination() {
        getMIP().setSolveParam(SolveParam.MIN_OBJ_VALUE, lowerBound);
        getMIP().setSolveParam(SolveParam.RELATIVE_OBJ_GAP, epsilon);
        getMIP().setSolveParam(SolveParam.DISPLAY_OUTPUT, displayOutput);
        try {
            IMIPResult mipResult = new SolverClient().solve(getMIP());
            intermediateSolutions = solveIntermediateSolutions(mipResult);
            return adaptMIPResult(mipResult);
        } catch (MIPException ex) {
            LOGGER.warn("WD failed", ex);
            throw new MIPException("MIP infeasible", ex);
        }
    }

    private List<Allocation> solveIntermediateSolutions(IMIPResult result) {
        if (result.getPoolSolutions() != null && !result.getPoolSolutions().isEmpty()) {
            LOGGER.debug("Found {} intermediate solutions candidates", result.getPoolSolutions().size());
            return result.getPoolSolutions().stream()
                    .filter(sol -> sol.getObjectiveValue() > lowerBound)
                    .filter(Predicate.isEqual(result).negate())
                    .distinct()
                    .sorted(Comparator.comparingDouble(PoolSolution::getObjectiveValue).reversed())
                    .map(this::adaptMIPResult)
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    protected abstract Allocation adaptMIPResult(ISolution mipResult);

    public List<Allocation> getIntermediateSolutions() {
        getAllocation();
        return intermediateSolutions;
    }

    public void setLowerBound(double lowerBound) {
        this.lowerBound = lowerBound;
    }

    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }

    public void setDisplayOutput(boolean displayOutput) {
        this.displayOutput = displayOutput;
    }
}