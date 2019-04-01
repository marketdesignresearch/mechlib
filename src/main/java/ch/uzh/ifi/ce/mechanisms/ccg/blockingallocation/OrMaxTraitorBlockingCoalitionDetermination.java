package ch.uzh.ifi.ce.mechanisms.ccg.blockingallocation;

import ch.uzh.ifi.ce.domain.Allocation;
import ch.uzh.ifi.ce.domain.AuctionInstance;
import ch.uzh.ifi.ce.mechanisms.AuctionResult;
import ch.uzh.ifi.ce.domain.Bidder;
import ch.uzh.ifi.ce.utils.PrecisionUtils;
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
