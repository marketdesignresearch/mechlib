package org.marketdesignresearch.mechlib.core.allocationlimits.validators;

import java.util.Set;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.allocationlimits.AllocationLimit;

public interface AllocationLimitHelper<T extends AllocationLimit> {
	boolean validate(T allocationLimit, Bundle bundle);
	int calculateAllocationBundleSpace(T allocationLimit, Set<? extends Good> startingSpace);
	Class<? extends AllocationLimit> getAllocationLimitType();
}
