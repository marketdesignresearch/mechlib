package org.marketdesignresearch.mechlib.core.allocationlimits;

public interface NoAllocationLimit extends AllocationLimit{
	default Class<? extends AllocationLimit> getType() {
		return NoAllocationLimit.class;
	}
}
