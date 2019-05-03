package org.marketdesignresearch.mechlib.winnerdetermination;

import edu.harvard.econcs.jopt.solver.mip.CompareType;
import edu.harvard.econcs.jopt.solver.mip.Constraint;
import edu.harvard.econcs.jopt.solver.mip.MIPWrapper;
import edu.harvard.econcs.jopt.solver.mip.Variable;
import org.marketdesignresearch.mechlib.domain.Bidder;
import org.marketdesignresearch.mechlib.domain.Bids;
import org.marketdesignresearch.mechlib.domain.BundleBid;
import org.marketdesignresearch.mechlib.domain.Good;

import java.util.HashMap;
import java.util.Map;

/**
 * Wraps an XOR winner determination
 * 
 * @author Benedikt Buenz
 * 
 */
public class XORWinnerDetermination extends BidBasedWinnerDetermination {
    private final Map<BundleBid, Variable> bidVariables = new HashMap<>();
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
                for (Map.Entry<Good, Integer> entry : bundleBid.getBundle().entrySet()) {
                    Constraint noDoubleAssignment = goods.computeIfAbsent(entry.getKey(), g -> new Constraint(CompareType.LEQ, g.available()));
                    noDoubleAssignment.addTerm(entry.getValue(), bidI);
                }
            }
            winnerDeterminationProgram.add(exclusiveBids);
        }

        goods.values().forEach(winnerDeterminationProgram::add);

        return winnerDeterminationProgram;
    }

    @Override
    public MIPWrapper getMIP() {
        return winnerDeterminationProgram;
    }

    @Override
    public Variable getBidVariable(BundleBid bundleBid) {
        return bidVariables.get(bundleBid);
    }
}
