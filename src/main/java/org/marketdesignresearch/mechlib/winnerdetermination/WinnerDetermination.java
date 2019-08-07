package org.marketdesignresearch.mechlib.winnerdetermination;

import com.google.common.collect.Lists;
import edu.harvard.econcs.jopt.solver.*;
import edu.harvard.econcs.jopt.solver.client.SolverClient;
import edu.harvard.econcs.jopt.solver.mip.MIP;
import edu.harvard.econcs.jopt.solver.mip.PoolSolution;
import edu.harvard.econcs.jopt.solver.mip.Variable;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentation;
import org.marketdesignresearch.mechlib.outcomerules.AllocationRule;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
public abstract class WinnerDetermination implements AllocationRule {

    private Allocation result = null;
    private List<Allocation> intermediateSolutions = null;
    @Getter
    private MipInstrumentation mipInstrumentation;
    @Getter
    private MipInstrumentation.MipPurpose purpose;

    protected WinnerDetermination() {
        this(MipInstrumentation.MipPurpose.ALLOCATION, new MipInstrumentation());
    }

    protected WinnerDetermination(MipInstrumentation.MipPurpose purpose) {
        this(purpose, new MipInstrumentation());
    }

    protected WinnerDetermination(MipInstrumentation.MipPurpose purpose, MipInstrumentation mipInstrumentation) {
        this.purpose = purpose;
        this.mipInstrumentation = mipInstrumentation;
    }

    /**
     * Defines the time limit for the solver.
     * What happens after the time limit is defined via {@link #setAcceptSuboptimal(boolean)}.
     *
     * @param timeLimit the time limit in seconds
     */
    @Setter
    private double timeLimit = -1.0;

    /**
     * Defines the behaviour in case the solver hits the defined timeout.
     *
     * @param acceptSuboptimal true: accept a suboptimal solution at timeout; false: throw an exception at timeout
     */
    @Setter
    private boolean acceptSuboptimal = true;

    @Setter
    private double lowerBound = -MIP.MAX_VALUE;
    @Setter
    private double epsilon = 1e-6;
    // TODO: private Map<SolveParam, Object> solveParams;
    // TODO: restoreDefaults()
    // TODO: Factory pattern?
    @Setter
    private double relativePoolMode4Tolerance = 0;
    @Setter
    private double absolutePoolMode4Tolerance = 0;
    @Setter
    private double timeLimitPoolMode4 = -1;
    @Setter
    private boolean displayOutput = false;

    protected abstract IMIP getMIP();

    @Override
    public Allocation getAllocation() {
        if (result == null) {
            result = solveWinnerDetermination();
        }
        return result;
    }

    protected Allocation solveWinnerDetermination() {
        getMIP().setSolveParam(SolveParam.MIN_OBJ_VALUE, lowerBound);
        if (timeLimit > 0) getMIP().setSolveParam(SolveParam.TIME_LIMIT, timeLimit);
        getMIP().setSolveParam(SolveParam.RELATIVE_OBJ_GAP, epsilon);
        getMIP().setSolveParam(SolveParam.DISPLAY_OUTPUT, displayOutput);
        getMIP().setSolveParam(SolveParam.ACCEPT_SUBOPTIMAL, acceptSuboptimal);
        try {
            IMIPResult mipResult = new SolverClient().solve(getMIP());
            intermediateSolutions = solveIntermediateSolutions(mipResult);
            Allocation bestAllocation = adaptMIPResult(mipResult);
            mipInstrumentation.postMIP(purpose, getMIP(), mipResult, bestAllocation, intermediateSolutions);
            return bestAllocation;
        } catch (MIPException ex) {
            log.warn("WD failed", ex);
            throw ex;
        }
    }

    private List<Allocation> solveIntermediateSolutions(IMIPResult result) {
        if (result.getPoolSolutions() != null && !result.getPoolSolutions().isEmpty()) {
            log.debug("Found {} intermediate solutions candidates", result.getPoolSolutions().size());
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
        getAllocation(); // FIXME: This does not work if the result has been calculated before
        return intermediateSolutions;
    }

    protected PoolMode getSolutionPoolMode() {
        return PoolMode.MODE_4;
    }

    public List<Allocation> getBestAllocations(int k) {
        return getBestAllocations(k, false);
    }

    public List<Allocation> getBestAllocations(int k, boolean allowNegative) {
        if (k == 1) return Lists.newArrayList(getAllocation());
        // This invalidates the current result, because otherwise previously collected solutions are returned
        this.result = null;
        double cut = lowerBound;
        if (cut < 0 && !allowNegative) {
            cut = 0;
        }
        getMIP().setSolveParam(SolveParam.SOLUTION_POOL_CAPACITY, k);
        getMIP().setSolveParam(SolveParam.MIN_OBJ_VALUE, cut);
        getMIP().setSolveParam(SolveParam.SOLUTION_POOL_MODE, getSolutionPoolMode().get());
        getMIP().setSolveParam(SolveParam.SOLUTION_POOL_MODE_4_ABSOLUTE_GAP_TOLERANCE, absolutePoolMode4Tolerance);
        getMIP().setSolveParam(SolveParam.SOLUTION_POOL_MODE_4_RELATIVE_GAP_TOLERANCE, relativePoolMode4Tolerance);
        getMIP().setSolveParam(SolveParam.SOLUTION_POOL_MODE_4_TIME_LIMIT, timeLimitPoolMode4);
        getMIP().setAdvancedVariablesOfInterest(getVariablesOfInterest());
        List<Allocation> allocations = getIntermediateSolutions();
        getMIP().setSolveParam(SolveParam.SOLUTION_POOL_MODE, 0);
        return allocations;
    }

    protected Collection<Collection<Variable>> getVariablesOfInterest() {
        return null;
    }

    /**
     * @see SolveParam#SOLUTION_POOL_MODE
     */
    public enum PoolMode {
        MODE_3,
        MODE_4;

        public int get() {
            switch (this) {
                case MODE_3:
                    return 3;
                case MODE_4:
                default:
                    return 4;
            }
        }
    }

}