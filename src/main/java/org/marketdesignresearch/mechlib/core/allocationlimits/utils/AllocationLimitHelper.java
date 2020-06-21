package org.marketdesignresearch.mechlib.core.allocationlimits.utils;

import java.util.List;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.allocationlimits.AllocationLimit;

public interface AllocationLimitHelper<T extends AllocationLimit> {
	boolean validate(T allocationLimit, Bundle bundle);
	int calculateAllocationBundleSpace(T allocationLimit, List<? extends Good> startingSpace);
	Class<? extends AllocationLimit> getAllocationLimitType();
}
