package org.marketdesignresearch.mechlib.utils;

import com.google.common.collect.Maps;
import edu.harvard.econcs.jopt.solver.IMIP;
import edu.harvard.econcs.jopt.solver.IMIPResult;
import edu.harvard.econcs.jopt.solver.SolveParam;
import edu.harvard.econcs.jopt.solver.server.cplex.CPlexMIPSolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public enum CPLEXUtils {
    SOLVER;
    private static final Logger LOGGER = LoggerFactory.getLogger(CPLEXUtils.class);
    private final CPlexMIPSolver solver = new CPlexMIPSolver();

    private final Map<SolveParam, Object> solveParamMap = Maps.newHashMap();

    public IMIPResult solve(IMIP program) {
        solveParamMap.forEach(program::setSolveParam);
        return solver.solve(program);
    }

    public void initializeSolveParams() {
        solveParamMap.put(SolveParam.CALC_DUALS, Boolean.TRUE);
        solveParamMap.put(SolveParam.LP_OPTIMIZATION_ALG, 2);
        solveParamMap.put(SolveParam.DISPLAY_OUTPUT, LOGGER.isDebugEnabled());
        solveParamMap.put(SolveParam.PARALLEL_MODE, 1);
        solveParamMap.put(SolveParam.ABSOLUTE_VAR_BOUND_GAP, 1e-9);
        solveParamMap.put(SolveParam.ABSOLUTE_OBJ_GAP, 0d);
        solveParamMap.put(SolveParam.RELATIVE_OBJ_GAP, 0d);
        solveParamMap.put(SolveParam.OBJ_TOLERANCE, 1e-9);
        solveParamMap.put(SolveParam.MARKOWITZ_TOLERANCE, .1);
        solveParamMap.put(SolveParam.CONSTRAINT_BACKOFF_LIMIT, 0);
        solveParamMap.put(SolveParam.PROBLEM_FILE, "");
        solveParamMap.put(SolveParam.TIME_LIMIT, (double) TimeUnit.SECONDS.convert(1, TimeUnit.HOURS));
        solveParamMap.put(SolveParam.SOLUTION_POOL_REPLACEMENT, 2);
    }

}
