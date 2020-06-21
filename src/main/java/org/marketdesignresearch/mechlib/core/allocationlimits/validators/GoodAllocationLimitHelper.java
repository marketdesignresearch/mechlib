package org.marketdesignresearch.mechlib.core.allocationlimits.validators;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.allocationlimits.AllocationLimit;
import org.marketdesignresearch.mechlib.core.allocationlimits.GoodAllocationLimit;

public class GoodAllocationLimitHelper implements AllocationLimitHelper<GoodAllocationLimit>{

	@Override
	public boolean validate(GoodAllocationLimit allocationLimit, Bundle bundle) {
		return bundle.getBundleEntries().stream().map(entry -> allocationLimit.getGoodAllocationLimit().contains(entry.getGood())).reduce(true, Boolean::logicalAnd).booleanValue();
	}

	@Override
	public Class<? extends AllocationLimit> getAllocationLimitType() {
		return GoodAllocationLimit.class;
	}

	@Override
	public int calculateAllocationBundleSpace(GoodAllocationLimit allocationLimit, List<? extends Good> startingSpace) {
		Set<? extends Good> allocatableGoods = new LinkedHashSet<>(startingSpace);
		allocatableGoods.retainAll(allocationLimit.getGoodAllocationLimit());
		return (int) Math.pow(2, allocatableGoods.stream().mapToInt(Good::getQuantity).sum());
	}

}
