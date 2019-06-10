package org.marketdesignresearch.mechlib.mechanisms.ccg.blockingallocation;


import org.marketdesignresearch.mechlib.domain.bid.Bids;
import org.marketdesignresearch.mechlib.mechanisms.MechanismResult;
import org.marketdesignresearch.mechlib.utils.PrecisionUtils;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public class IntermediateSolutionsAllocationFinderFactory implements BlockingAllocationFinder {
    private static final Logger LOGGER = LoggerFactory.getLogger(IntermediateSolutionsAllocationFinderFactory.class);
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
    public BlockingAllocation findBlockingAllocation(Bids bids, MechanismResult priorResult) {
        WinnerDetermination blockingCoalitionFinder = new MultiBlockingAllocationsDetermination(bids, priorResult, mode);
        blockingCoalitionFinder.setLowerBound(priorResult.getPayment().getTotalPayments().subtract(epsilon).doubleValue());
        LOGGER.debug("Found {} intermediate solutions", blockingCoalitionFinder.getIntermediateSolutions().size());
        return new BlockingAllocation(blockingCoalitionFinder.getAllocation(), blockingCoalitionFinder.getIntermediateSolutions());
    }

}
