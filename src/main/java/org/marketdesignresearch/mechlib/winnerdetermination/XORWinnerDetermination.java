package org.marketdesignresearch.mechlib.winnerdetermination;

import com.google.common.base.Preconditions;
import edu.harvard.econcs.jopt.solver.mip.CompareType;
import edu.harvard.econcs.jopt.solver.mip.Constraint;
import edu.harvard.econcs.jopt.solver.mip.MIPWrapper;
import edu.harvard.econcs.jopt.solver.mip.Variable;
import org.marketdesignresearch.mechlib.core.BundleBid;
import org.marketdesignresearch.mechlib.core.BundleEntry;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;

import java.util.HashMap;
import java.util.Map;

/**
 * Wraps an XOR winner determination
 * 
 * @author Benedikt Buenz
 * 
 */
public class XORWinnerDetermination extends BidBasedWinnerDetermination {

    private final MIPWrapper winnerDeterminationProgram;

    public XORWinnerDetermination(Bids bids) {
        super(bids);
        winnerDeterminationProgram = createWinnerDeterminationMIP(bids);
    }

    private MIPWrapper createWinnerDeterminationMIP(Bids bids) {
        MIPWrapper winnerDeterminationProgram = MIPWrapper.makeNewMaxMIP();
        // Add decision variables and objective terms:
        Map<Good, Constraint> goods = new HashMap<>();
        for (Bidder bidder : bids.getBidders()) {
            Constraint exclusiveBids = new Constraint(CompareType.LEQ, 1);

            for (BundleBid bundleBid : bids.getBid(bidder).getBundleBids()) {

                Variable bidI = winnerDeterminationProgram.makeNewBooleanVar("Bid_" + bundleBid.getId());
                bidVariables.put(bundleBid, bidI);
                double bidAmount = bundleBid.getAmount().doubleValue();
                winnerDeterminationProgram.addObjectiveTerm(bidAmount, bidI);
                exclusiveBids.addTerm(1, bidI);
                for (BundleEntry entry : bundleBid.getBundle().getBundleEntries()) {
                    Constraint noDoubleAssignment = goods.computeIfAbsent(entry.getGood(), g -> new Constraint(CompareType.LEQ, g.getQuantity()));
                    noDoubleAssignment.addTerm(entry.getAmount(), bidI);
                }
            }
            winnerDeterminationProgram.add(exclusiveBids);
        }

        goods.values().forEach(winnerDeterminationProgram::add);

        return winnerDeterminationProgram;
    }

    @Override
    public WinnerDetermination join(WinnerDetermination other) {
        Preconditions.checkArgument(other instanceof BidBasedWinnerDetermination);
        BidBasedWinnerDetermination otherBidBased = (BidBasedWinnerDetermination) other;
        return new XORWinnerDetermination(otherBidBased.getBids().join(getBids()));
    }

    @Override
    public MIPWrapper getMIP() {
        return winnerDeterminationProgram;
    }

}
