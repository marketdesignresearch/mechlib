package org.marketdesignresearch.mechlib.utils;

import com.google.common.collect.Maps;
import edu.harvard.econcs.jopt.solver.IMIP;
import edu.harvard.econcs.jopt.solver.IMIPResult;
import edu.harvard.econcs.jopt.solver.SolveParam;
import edu.harvard.econcs.jopt.solver.server.cplex.CPlexMIPSolver;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * A wrapper around the solving logic. By default, all solves in MechLib are solved through this singleton,
 * which allows setting some parameters that are applied consistently among all MIPs that are solved inside the MechLib.
 * E.g., you can specify a thread count here, and this will be applied to all MIPs that are to be solved.
 * Parameters that are set on {@link IMIP}-level are NOT overwritten.
 *
 * This wrapper also exposes some methods to set default parameters in a way they have been set in experiments before.
 */
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
     *
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
     * You can add additional (or override previously added) default parameters that are then applied to any future program
     * that does not define these solve parameters by itself already.
     * @param param The SolveParam to be specified
     * @param value The value that should be set for the SolveParam
     */
    public void setSolveParam(SolveParam param, Object value) {
        solveParamMap.put(param, value);
    }

    /**
     * Clears the previously added solve parameters
     */
    public void clearSolveParams() {
        solveParamMap.clear();
    }

    /**
     *  A helper function to initialize the solver with some reasonable default parameters.
     *  Has to be actively called by the user.
     *  This sets the following:
     *  <ul>
     *      <li>DISPLAY_OUTPUT: according to the log level - only if the debug log level is enabled, it's set to true</li>
     *      <li>THREADS: 1</li>
     *      <li>TIME_LIMIT: 1 hour</li>
     *  </ul>
     */
    public void exampleSolveParams() {
        clearSolveParams();
        setSolveParam(SolveParam.DISPLAY_OUTPUT, log.isDebugEnabled());
        setSolveParam(SolveParam.THREADS, 1);
        setSolveParam(SolveParam.TIME_LIMIT, (double) TimeUnit.SECONDS.convert(1, TimeUnit.HOURS));
    }

    /**
     * A helper function to initialize the solver with default parameters that were previously used in experiments
     * of very specific CCG-price calculation MIPs
     */
    public void exampleNormSolveParams() {
        exampleSolveParams();
        setSolveParam(SolveParam.CALC_DUALS, Boolean.TRUE);
        setSolveParam(SolveParam.LP_OPTIMIZATION_ALG, 2);
        setSolveParam(SolveParam.PARALLEL_MODE, 1);
        setSolveParam(SolveParam.ABSOLUTE_VAR_BOUND_GAP, 1e-9);
        setSolveParam(SolveParam.ABSOLUTE_OBJ_GAP, 0d);
        setSolveParam(SolveParam.RELATIVE_OBJ_GAP, 0d);
        setSolveParam(SolveParam.OBJ_TOLERANCE, 1e-9);
        setSolveParam(SolveParam.MARKOWITZ_TOLERANCE, .1);
        setSolveParam(SolveParam.CONSTRAINT_BACKOFF_LIMIT, 0);
        setSolveParam(SolveParam.PROBLEM_FILE, "");
        setSolveParam(SolveParam.SOLUTION_POOL_REPLACEMENT, 2);
    }

}
