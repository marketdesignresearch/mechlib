package org.marketdesignresearch.mechlib.mechanisms.ccg.blockingallocation;

import edu.harvard.econcs.jopt.solver.mip.MIPWrapper;
import edu.harvard.econcs.jopt.solver.mip.Variable;
import org.marketdesignresearch.mechlib.domain.Allocation;
import org.marketdesignresearch.mechlib.domain.bidder.Bidder;
import org.marketdesignresearch.mechlib.domain.bid.Bids;
import org.marketdesignresearch.mechlib.mechanisms.MechanismResult;
import org.marketdesignresearch.mechlib.utils.PrecisionUtils;

public class OrMaxTraitorBlockingCoalitionDetermination extends BlockingCoalitionDetermination {

    public OrMaxTraitorBlockingCoalitionDetermination(Bids bids, MechanismResult previousMechanismResult) {
        super(bids, previousMechanismResult);
        MIPWrapper mip = getMIP();
        Allocation previousAllocation = previousMechanismResult.getAllocation();
        for (Bidder winningBidder : previousAllocation.getWinners()) {
            Variable traitor = mip.getVar(TRAITOR + winningBidder.getId());
            // EPSILON adds Secondary Objective
            mip.addObjectiveTerm(PrecisionUtils.EPSILON.doubleValue(), traitor);
        }
    }
}
