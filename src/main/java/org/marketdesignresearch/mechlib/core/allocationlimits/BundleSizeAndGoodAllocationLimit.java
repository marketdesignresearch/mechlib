package org.marketdesignresearch.mechlib.core.allocationlimits;

public interface BundleSizeAndGoodAllocationLimit extends BundleSizeAllocationLimit, GoodAllocationLimit{
	default Class<? extends AllocationLimit> getType() {
		return BundleSizeAndGoodAllocationLimit.class;
	}
}
