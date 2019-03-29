package ch.uzh.ifi.ce.mechanisms.ccg.paymentrules;

import ch.uzh.ifi.ce.domain.Bidder;
import ch.uzh.ifi.ce.domain.BidderPayment;
import ch.uzh.ifi.ce.domain.Payment;
import ch.uzh.ifi.ce.mechanisms.MetaInfo;
import ch.uzh.ifi.ce.utils.CPLEXUtils;
import edu.harvard.econcs.jopt.solver.IMIP;
import edu.harvard.econcs.jopt.solver.IMIPResult;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class PaymentNorm {
    protected static final String BIDDER = "bidder_";

    protected void setProposedValues(IMIPResult result, IMIP mip) {
        result.getValues().forEach((n, v) -> mip.proposeValue(mip.getVar(n), v));

    }

    protected IMIPResult solveProgram(IMIP program) {
        return CPLEXUtils.SOLVER.solve(program);
    }

    public final Payment adaptProgram(Set<Bidder> winners, IMIPResult mipResult, MetaInfo metaInfo) {
        Map<Bidder, BidderPayment> newBidderPayments = new HashMap<>();
        for (Bidder winningBidder : winners) {
            String variableId = BIDDER + winningBidder.getId();
            BigDecimal newPayment = BigDecimal.ZERO;

            if (mipResult.getValues().containsKey(variableId)) {
                newPayment = BigDecimal.valueOf(mipResult.getValue(variableId));
            }
            newBidderPayments.put(winningBidder, new BidderPayment(newPayment));

        }

        return new Payment(newBidderPayments, metaInfo);
    }

    /**
     * adds the norms objective to the program may also add additional
     * constraints
     * 
     * @param program
     * @return something but mostly nothing really bad solution
     */
    public abstract Object addNormObjective(IMIP program);
}
