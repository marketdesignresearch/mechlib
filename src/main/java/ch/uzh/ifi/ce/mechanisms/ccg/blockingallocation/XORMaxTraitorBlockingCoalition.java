package ch.uzh.ifi.ce.mechanisms.ccg.blockingallocation;

import ch.uzh.ifi.ce.domain.*;
import ch.uzh.ifi.ce.mechanisms.AuctionResult;
import ch.uzh.ifi.ce.winnerdetermination.XORWinnerDetermination;
import ch.uzh.ifi.ce.utils.PrecisionUtils;
import edu.harvard.econcs.jopt.solver.mip.Constraint;
import edu.harvard.econcs.jopt.solver.mip.MIPWrapper;
import edu.harvard.econcs.jopt.solver.mip.Variable;

public class XORMaxTraitorBlockingCoalition extends XORWinnerDetermination {

    public XORMaxTraitorBlockingCoalition(AuctionInstance auctionInstance, AuctionResult previousAuctionResult) {
        super(auctionInstance);
        MIPWrapper mip = getMIP();
        Allocation previousAllocation = previousAuctionResult.getAllocation();
        for (Bidder winningBidder : previousAllocation.getWinners()) {
            Variable traitor = mip.makeNewBooleanVar("Traitor_" + winningBidder.getId());
            // EPSILON adds Secondary Objective
            mip.addObjectiveTerm(PrecisionUtils.EPSILON.scaleByPowerOfTen(-1).doubleValue(), traitor);
            // traitor is 1 if at least one bundleBid of the bidder was
            // allocated else 0
            Constraint bundleAssigned = mip.beginNewEQConstraint(0.0);
            for (BundleBid bundleBid : auctionInstance.getBid(winningBidder).getBundleBids()) {
                bundleAssigned.addTerm(1.0, getBidVariable(bundleBid));
            }
            bundleAssigned.addTerm(-1, traitor);
            mip.add(bundleAssigned);

        }
    }

}
