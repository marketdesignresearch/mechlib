package org.marketdesignresearch.mechlib.utils;

import com.google.common.collect.Maps;
import edu.harvard.econcs.jopt.solver.IMIP;
import edu.harvard.econcs.jopt.solver.IMIPResult;
import edu.harvard.econcs.jopt.solver.SolveParam;
import edu.harvard.econcs.jopt.solver.server.cplex.CPlexMIPSolver;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public enum CPLEXUtils {
    SOLVER;

    private final CPlexMIPSolver solver = new CPlexMIPSolver();

    private final Map<SolveParam, Object> solveParamMap = Maps.newHashMap();

    /**
     * Solves the program.
     * It applies the solve parameters that are specified in {@link #solveParamMap} only if they are not already
     * specified in the program itself.
     * Careful: JOpt has a default parameter logic as well, so often the program already includes JOpt-specific default
     * parameters, which will not be overwritten here. To be sure, you can call {@link IMIP#clearSolveParams()} before
     * setting your own parameters
     * @param program The program to be solved
     * @return The result object
     */
    public IMIPResult solve(IMIP program) {
        solveParamMap.forEach((param , value) -> {
            if (!program.isSolveParamSpecified(param)) {
                program.setSolveParam(param, value);
            }
        });
        return solver.solve(program);
    }

    /**
     * You can add additional (or override existing) default parameters that are then applied to any future program
     * that does not define these solve parameters by itself already.
     * @param param The SolveParam to be specified
     * @param value The value that should be set for the SolveParam
     */
    public void setSolveParam(SolveParam param, Object value) {
        solveParamMap.put(param, value);
    }


    /**
     *  A helper function to initialize the solver with some reasonable default parameters.
     *  Has to be actively called by the user.
     */
    public void initializeSolveParams() {
        solveParamMap.clear();
        solveParamMap.put(SolveParam.DISPLAY_OUTPUT, log.isDebugEnabled());
        solveParamMap.put(SolveParam.THREADS, 1);
        solveParamMap.put(SolveParam.TIME_LIMIT, (double) TimeUnit.SECONDS.convert(1, TimeUnit.HOURS));
    }

    /**
     * A helper function to initialize the solver with default parameters that were previously used in experiments
     * of very specific CCG-price calculation MIPs
     */
    public void initializeNormSolveParams() {
        initializeSolveParams();
        solveParamMap.put(SolveParam.CALC_DUALS, Boolean.TRUE);
        solveParamMap.put(SolveParam.LP_OPTIMIZATION_ALG, 2);
        solveParamMap.put(SolveParam.PARALLEL_MODE, 1);
        solveParamMap.put(SolveParam.ABSOLUTE_VAR_BOUND_GAP, 1e-9);
        solveParamMap.put(SolveParam.ABSOLUTE_OBJ_GAP, 0d);
        solveParamMap.put(SolveParam.RELATIVE_OBJ_GAP, 0d);
        solveParamMap.put(SolveParam.OBJ_TOLERANCE, 1e-9);
        solveParamMap.put(SolveParam.MARKOWITZ_TOLERANCE, .1);
        solveParamMap.put(SolveParam.CONSTRAINT_BACKOFF_LIMIT, 0);
        solveParamMap.put(SolveParam.PROBLEM_FILE, "");
        solveParamMap.put(SolveParam.SOLUTION_POOL_REPLACEMENT, 2);
    }

}
