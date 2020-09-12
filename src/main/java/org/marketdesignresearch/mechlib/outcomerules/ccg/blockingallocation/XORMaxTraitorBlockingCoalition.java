package org.marketdesignresearch.mechlib.outcomerules.ccg.blockingallocation;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValuePair;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.utils.PrecisionUtils;
import org.marketdesignresearch.mechlib.winnerdetermination.XORWinnerDetermination;

import edu.harvard.econcs.jopt.solver.mip.Constraint;
import edu.harvard.econcs.jopt.solver.mip.MIPWrapper;
import edu.harvard.econcs.jopt.solver.mip.Variable;

public class XORMaxTraitorBlockingCoalition extends XORWinnerDetermination {

	public XORMaxTraitorBlockingCoalition(BundleValueBids<?> bids, Outcome previousOutcome) {
		super(bids);
		MIPWrapper mip = getMIP();
		Allocation previousAllocation = previousOutcome.getAllocation();
		for (Bidder winningBidder : previousAllocation.getWinners()) {
			Variable traitor = mip.makeNewBooleanVar("Traitor_" + winningBidder.getId());
			// EPSILON adds Secondary Objective
			mip.addObjectiveTerm(PrecisionUtils.EPSILON.scaleByPowerOfTen(-1).doubleValue(), traitor);
			// traitor is 1 if at least one bundleBid of the bidder was
			// allocated else 0
			Constraint bundleAssigned = mip.beginNewEQConstraint(0.0);
			for (BundleExactValuePair bundleBid : bids.getBid(winningBidder).getBundleBids()) {
				bundleAssigned.addTerm(1.0, getBidVariable(bundleBid));
			}
			bundleAssigned.addTerm(-1, traitor);
			mip.add(bundleAssigned);

		}
	}

}
