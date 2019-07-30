package org.marketdesignresearch.mechlib.outcomerules.ccg.blockingallocation;

import edu.harvard.econcs.jopt.solver.mip.MIPWrapper;
import edu.harvard.econcs.jopt.solver.mip.Variable;
import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.utils.PrecisionUtils;

public class OrMaxTraitorBlockingCoalitionDetermination extends BlockingCoalitionDetermination {

    public OrMaxTraitorBlockingCoalitionDetermination(Bids bids, Outcome previousOutcome) {
        super(bids, previousOutcome);
        MIPWrapper mip = getMIP();
        Allocation previousAllocation = previousOutcome.getAllocation();
        for (Bidder winningBidder : previousAllocation.getWinners()) {
            Variable traitor = mip.getVar(TRAITOR + winningBidder.getId());
            // EPSILON adds Secondary Objective
            mip.addObjectiveTerm(PrecisionUtils.EPSILON.doubleValue(), traitor);
        }
    }
}
