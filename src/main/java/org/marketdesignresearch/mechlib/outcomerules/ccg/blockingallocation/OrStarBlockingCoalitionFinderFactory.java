package org.marketdesignresearch.mechlib.outcomerules.ccg.blockingallocation;

import java.math.BigDecimal;

import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.utils.PrecisionUtils;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;

public class OrStarBlockingCoalitionFinderFactory implements BlockingAllocationFinder {
	/**
	 * 
	 */
	private final BigDecimal epsilon;

	public OrStarBlockingCoalitionFinderFactory(BigDecimal epsilon) {
		this.epsilon = epsilon;
	}

	public OrStarBlockingCoalitionFinderFactory() {
		this.epsilon = PrecisionUtils.EPSILON.scaleByPowerOfTen(2);
	}

	@Override
	public BlockingAllocation findBlockingAllocation(BundleValueBids<?> bids, Outcome priorResult) {
		WinnerDetermination blockingCoalitionFinder = new BlockingCoalitionDetermination(bids, priorResult);
		blockingCoalitionFinder
				.setLowerBound(priorResult.getPayment().getTotalPayments().subtract(epsilon).doubleValue());
		return BlockingAllocation.of(blockingCoalitionFinder.getAllocation());
	}

}
