package org.marketdesignresearch.mechlib.core.allocationlimits.validators;

import java.util.Set;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.allocationlimits.AllocationLimit;
import org.marketdesignresearch.mechlib.core.allocationlimits.NoAllocationLimit;

public class NoAllocationLimitHelper implements AllocationLimitHelper<NoAllocationLimit>{

	@Override
	public boolean validate(NoAllocationLimit allocationLimit, Bundle bundle) {
		return true;
	}

	@Override
	public Class<? extends AllocationLimit> getAllocationLimitType() {
		return NoAllocationLimit.class;
	}

	@Override
	public int calculateAllocationBundleSpace(NoAllocationLimit allocationLimit, Set<? extends Good> startingSpace) {
		return (int) Math.pow(2, startingSpace.stream().mapToInt(Good::getQuantity).sum());
	}

}
