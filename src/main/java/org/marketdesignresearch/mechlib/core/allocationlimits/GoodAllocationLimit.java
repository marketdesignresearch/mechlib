package org.marketdesignresearch.mechlib.core.allocationlimits;

import java.util.List;

import org.marketdesignresearch.mechlib.core.Good;

public interface GoodAllocationLimit extends AllocationLimit{
	
	/**
	 * @return the set of good a bidder is able to acquire or null if there is no 
	 * limit for this specific bidder
	 */
	List<? extends Good> getGoodAllocationLimit();
	
	default Class<? extends AllocationLimit> getType() {
		return GoodAllocationLimit.class;
	}
}
