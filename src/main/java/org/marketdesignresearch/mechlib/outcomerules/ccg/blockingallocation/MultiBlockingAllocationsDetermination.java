package org.marketdesignresearch.mechlib.outcomerules.ccg.blockingallocation;

import edu.harvard.econcs.jopt.solver.SolveParam;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.Outcome;

public class MultiBlockingAllocationsDetermination extends BlockingCoalitionDetermination {
    public enum Mode {
        JUST_ASK, POOL_5, POOl_10, POOl_15, POOL_30, POOl_50
    }

    public MultiBlockingAllocationsDetermination(Bids bids, Outcome previousOutcome, Mode mode) {
        super(bids, previousOutcome);
        switch (mode) {
        case JUST_ASK:
            getMIP().setSolveParam(SolveParam.POPULATE_LIMIT, 100000);
            getMIP().setSolveParam(SolveParam.SOLUTION_POOL_CAPACITY, 2000);
            getMIP().setSolveParam(SolveParam.SOLUTION_POOL_INTENSITY, 1);
            getMIP().setSolveParam(SolveParam.SOLUTION_POOL_MODE, 0);
            break;
        case POOL_5:
            getMIP().setSolveParam(SolveParam.POPULATE_LIMIT, 100000);
            getMIP().setSolveParam(SolveParam.SOLUTION_POOL_CAPACITY, 2000);
            getMIP().setSolveParam(SolveParam.SOLUTION_POOL_INTENSITY, 2);
            getMIP().setSolveParam(SolveParam.RELATIVE_POOL_SOLVE_TIME, .05d);
            getMIP().setSolveParam(SolveParam.SOLUTION_POOL_MODE, 2);
            break;
        case POOl_10:
            getMIP().setSolveParam(SolveParam.POPULATE_LIMIT, 100000);
            getMIP().setSolveParam(SolveParam.SOLUTION_POOL_CAPACITY, 2000);
            getMIP().setSolveParam(SolveParam.SOLUTION_POOL_INTENSITY, 2);
            getMIP().setSolveParam(SolveParam.RELATIVE_POOL_SOLVE_TIME, 0.10d);
            getMIP().setSolveParam(SolveParam.SOLUTION_POOL_MODE, 2);
            break;

        case POOl_15:
            getMIP().setSolveParam(SolveParam.POPULATE_LIMIT, 100000);
            getMIP().setSolveParam(SolveParam.SOLUTION_POOL_CAPACITY, 2000);
            getMIP().setSolveParam(SolveParam.SOLUTION_POOL_INTENSITY, 2);
            getMIP().setSolveParam(SolveParam.RELATIVE_POOL_SOLVE_TIME, 0.15d);
            getMIP().setSolveParam(SolveParam.SOLUTION_POOL_MODE, 2);
            break;
        case POOL_30:
            getMIP().setSolveParam(SolveParam.POPULATE_LIMIT, 100000);
            getMIP().setSolveParam(SolveParam.SOLUTION_POOL_CAPACITY, 2000);
            getMIP().setSolveParam(SolveParam.SOLUTION_POOL_INTENSITY, 2);
            getMIP().setSolveParam(SolveParam.RELATIVE_POOL_SOLVE_TIME, .3d);
            getMIP().setSolveParam(SolveParam.SOLUTION_POOL_MODE, 2);
            break;
        case POOl_50:
            getMIP().setSolveParam(SolveParam.POPULATE_LIMIT, 100000);
            getMIP().setSolveParam(SolveParam.SOLUTION_POOL_CAPACITY, 4000);
            getMIP().setSolveParam(SolveParam.SOLUTION_POOL_INTENSITY, 2);
            getMIP().setSolveParam(SolveParam.RELATIVE_POOL_SOLVE_TIME, 0.5d);
            getMIP().setSolveParam(SolveParam.SOLUTION_POOL_MODE, 2);
            break;
        default:
            break;

        }

    }
}
