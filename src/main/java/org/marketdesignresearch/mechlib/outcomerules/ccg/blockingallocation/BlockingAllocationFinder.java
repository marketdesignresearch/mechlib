package org.marketdesignresearch.mechlib.outcomerules.ccg.blockingallocation;

import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;

public interface BlockingAllocationFinder {
	BlockingAllocation findBlockingAllocation(BundleValueBids<?> bids, Outcome priorResult);

}
