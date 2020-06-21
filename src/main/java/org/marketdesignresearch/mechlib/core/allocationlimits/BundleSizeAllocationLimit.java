package org.marketdesignresearch.mechlib.core.allocationlimits;

import org.marketdesignresearch.mechlib.core.bidder.Bidder;

public interface BundleSizeAllocationLimit extends AllocationLimit{
	
	/**
	 * @return the number of items a bidder can acquire at most
	 */
	int getBundleSizeLimit();
	
	default Class<? extends AllocationLimit> getType() {
		return BundleSizeAllocationLimit.class;
	}
}
