package org.marketdesignresearch.mechlib.mechanisms.ccg.blockingallocation;

import edu.harvard.econcs.jopt.solver.mip.Constraint;
import edu.harvard.econcs.jopt.solver.mip.MIPWrapper;
import edu.harvard.econcs.jopt.solver.mip.Variable;
import org.marketdesignresearch.mechlib.domain.Allocation;
import org.marketdesignresearch.mechlib.domain.Bidder;
import org.marketdesignresearch.mechlib.domain.Bids;
import org.marketdesignresearch.mechlib.domain.BundleBid;
import org.marketdesignresearch.mechlib.mechanisms.AuctionResult;
import org.marketdesignresearch.mechlib.utils.PrecisionUtils;
import org.marketdesignresearch.mechlib.winnerdetermination.XORWinnerDetermination;

public class XORMaxTraitorBlockingCoalition extends XORWinnerDetermination {

    public XORMaxTraitorBlockingCoalition(Bids bids, AuctionResult previousAuctionResult) {
        super(bids);
        MIPWrapper mip = getMIP();
        Allocation previousAllocation = previousAuctionResult.getAllocation();
        for (Bidder winningBidder : previousAllocation.getWinners()) {
            Variable traitor = mip.makeNewBooleanVar("Traitor_" + winningBidder.getId());
            // EPSILON adds Secondary Objective
            mip.addObjectiveTerm(PrecisionUtils.EPSILON.scaleByPowerOfTen(-1).doubleValue(), traitor);
            // traitor is 1 if at least one bundleBid of the bidder was
            // allocated else 0
            Constraint bundleAssigned = mip.beginNewEQConstraint(0.0);
            for (BundleBid bundleBid : bids.getBid(winningBidder).getBundleBids()) {
                bundleAssigned.addTerm(1.0, getBidVariable(bundleBid));
            }
            bundleAssigned.addTerm(-1, traitor);
            mip.add(bundleAssigned);

        }
    }

}
