package org.marketdesignresearch.mechlib.outcomerules.ccg.paymentrules;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.marketdesignresearch.mechlib.core.BidderPayment;
import org.marketdesignresearch.mechlib.core.Payment;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentation;
import org.marketdesignresearch.mechlib.metainfo.MetaInfo;
import org.marketdesignresearch.mechlib.utils.CPLEXUtils;

import edu.harvard.econcs.jopt.solver.IMIP;
import edu.harvard.econcs.jopt.solver.IMIPResult;
import lombok.Getter;
import lombok.Setter;

public abstract class PaymentNorm implements CorePaymentNorm {
    protected static final String BIDDER = "bidder_";

    protected void setProposedValues(IMIPResult result, IMIP mip) {
        result.getValues().forEach((n, v) -> mip.proposeValue(mip.getVar(n), v));

    }

    protected IMIPResult solveProgram(IMIP program) {
        IMIPResult result = CPLEXUtils.SOLVER.solve(program);
        getMipInstrumentation().postMIP(MipInstrumentation.MipPurpose.PAYMENT, program, result);
        return result;
    }

    public final Payment adaptProgram(Set<? extends Bidder> winners, IMIPResult mipResult, MetaInfo metaInfo) {
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

    // region instrumentation
    @Getter @Setter
    private MipInstrumentation mipInstrumentation = MipInstrumentation.NO_OP;
    // endregion
}
