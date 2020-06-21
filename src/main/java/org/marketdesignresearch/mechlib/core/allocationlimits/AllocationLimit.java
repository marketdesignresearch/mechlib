package org.marketdesignresearch.mechlib.core.allocationlimits;

public interface AllocationLimit {
	
	public static AllocationLimit NO = new NoAllocationLimit() {};
	
	Class<? extends AllocationLimit> getType();
}
