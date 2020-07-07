package org.marketdesignresearch.mechlib.outcomerules.ccg.blockingallocation;

import java.math.BigDecimal;

import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.utils.PrecisionUtils;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;

public class XORBlockingCoalitionFinderFactory implements BlockingAllocationFinder {
	/**
	 * 
	 */
	private final BigDecimal epsilon;

	public XORBlockingCoalitionFinderFactory(BigDecimal epsilon) {
		this.epsilon = epsilon;
	}

	public XORBlockingCoalitionFinderFactory() {
		this.epsilon = PrecisionUtils.EPSILON.scaleByPowerOfTen(2);
	}

	@Override
	public BlockingAllocation findBlockingAllocation(BundleValueBids<?> bids, Outcome priorResult) {
		BundleValueBids<?> reducedBids = bids.reducedBy(priorResult);

		WinnerDetermination blockingCoalitionFinder = new XORBlockingCoalitionDetermination(reducedBids);
		double lowerBound = priorResult.getPayment().getTotalPayments().subtract(epsilon).doubleValue();
		blockingCoalitionFinder.setLowerBound(lowerBound);
		return BlockingAllocation.of(blockingCoalitionFinder.getAllocation());
	}

}
