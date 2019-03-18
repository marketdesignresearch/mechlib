package ch.uzh.ifi.ce.mechanisms.winnerdetermination;

import ch.uzh.ifi.ce.domain.AuctionInstance;
import ch.uzh.ifi.ce.domain.Bidder;
import ch.uzh.ifi.ce.domain.BundleBid;
import ch.uzh.ifi.ce.domain.Good;
import ch.uzh.ifi.ce.mechanisms.Allocator;
import edu.harvard.econcs.jopt.solver.mip.CompareType;
import edu.harvard.econcs.jopt.solver.mip.Constraint;
import edu.harvard.econcs.jopt.solver.mip.MIPWrapper;
import edu.harvard.econcs.jopt.solver.mip.Variable;

import java.util.HashMap;
import java.util.Map;

/**
 * Wraps an XOR winner determination
 * 
 * @author Benedikt Buenz
 * 
 */
public class XORWinnerDetermination extends WinnerDetermination implements Allocator {
    private final Map<BundleBid, Variable> bidVariables = new HashMap<>();
    private final MIPWrapper winnerDeterminationProgram;

    public XORWinnerDetermination(AuctionInstance auctionInstance) {
        super(auctionInstance);
        winnerDeterminationProgram = createWinnerDeterminationMIP(auctionInstance);
    }

    private MIPWrapper createWinnerDeterminationMIP(AuctionInstance auctionInstance) {
        MIPWrapper winnerDeterminationProgram = MIPWrapper.makeNewMaxMIP();
        // Add decision variables and objective terms:
        Map<Good, Constraint> goods = new HashMap<>();
        for (Bidder bidder : auctionInstance.getBidders()) {
            Constraint exclusiveBids = new Constraint(CompareType.LEQ, 1);

            for (BundleBid bundleBid : auctionInstance.getBid(bidder).getBundleBids()) {

                Variable bidI = winnerDeterminationProgram.makeNewBooleanVar("Bid_" + bundleBid.getId());
                bidVariables.put(bundleBid, bidI);
                double bidAmount = bundleBid.getAmount().doubleValue();
                winnerDeterminationProgram.addObjectiveTerm(bidAmount, bidI);
                exclusiveBids.addTerm(1, bidI);
                for (Good good : bundleBid.getBundle()) {
                    Constraint noDoubleAssignment = goods.computeIfAbsent(good, g -> new Constraint(CompareType.LEQ, 1));
                    noDoubleAssignment.addTerm(1.0, bidI);
                }
            }
            winnerDeterminationProgram.add(exclusiveBids);
        }

        goods.values().forEach(winnerDeterminationProgram::add);

        return winnerDeterminationProgram;
    }

    @Override
    protected MIPWrapper getMIP() {
        return winnerDeterminationProgram;
    }

    @Override
    protected Variable getBidVariable(BundleBid bundleBid) {
        return bidVariables.get(bundleBid);
    }
}
