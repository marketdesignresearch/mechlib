package org.marketdesignresearch.mechlib.outcomerules.ccg.blockingallocation;


import lombok.extern.slf4j.Slf4j;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.utils.PrecisionUtils;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;

import java.math.BigDecimal;

@Slf4j
public class IntermediateSolutionsAllocationFinderFactory implements BlockingAllocationFinder {

    /**
     * 
     */
    private final BigDecimal epsilon;
    private final MultiBlockingAllocationsDetermination.Mode mode;

    public IntermediateSolutionsAllocationFinderFactory(MultiBlockingAllocationsDetermination.Mode mode, BigDecimal epsilon) {
        this.epsilon = epsilon;
        this.mode = mode;
    }

    public IntermediateSolutionsAllocationFinderFactory(MultiBlockingAllocationsDetermination.Mode mode) {
        this(mode, PrecisionUtils.EPSILON);
    }

    @Override
    public BlockingAllocation findBlockingAllocation(Bids bids, Outcome priorResult) {
        WinnerDetermination blockingCoalitionFinder = new MultiBlockingAllocationsDetermination(bids, priorResult, mode);
        blockingCoalitionFinder.setLowerBound(priorResult.getPayment().getTotalPayments().subtract(epsilon).doubleValue());
        log.debug("Found {} intermediate solutions", blockingCoalitionFinder.getIntermediateSolutions().size());
        return new BlockingAllocation(blockingCoalitionFinder.getAllocation(), blockingCoalitionFinder.getIntermediateSolutions());
    }

}
