package org.marketdesignresearch.mechlib.mechanisms.ccg.blockingallocation;

import org.marketdesignresearch.mechlib.domain.Allocation;
import org.marketdesignresearch.mechlib.domain.AuctionInstance;
import org.marketdesignresearch.mechlib.mechanisms.AuctionResult;
import org.marketdesignresearch.mechlib.domain.Bidder;
import org.marketdesignresearch.mechlib.utils.PrecisionUtils;
import edu.harvard.econcs.jopt.solver.mip.MIPWrapper;
import edu.harvard.econcs.jopt.solver.mip.Variable;

public class OrMaxTraitorBlockingCoalitionDetermination extends BlockingCoalitionDetermination {

    public OrMaxTraitorBlockingCoalitionDetermination(AuctionInstance auctionInstance, AuctionResult previousAuctionResult) {
        super(auctionInstance, previousAuctionResult);
        MIPWrapper mip = getMIP();
        Allocation previousAllocation = previousAuctionResult.getAllocation();
        for (Bidder winningBidder : previousAllocation.getWinners()) {
            Variable traitor = mip.getVar(TRAITOR + winningBidder.getId());
            // EPSILON adds Secondary Objective
            mip.addObjectiveTerm(PrecisionUtils.EPSILON.doubleValue(), traitor);
        }
    }
}
