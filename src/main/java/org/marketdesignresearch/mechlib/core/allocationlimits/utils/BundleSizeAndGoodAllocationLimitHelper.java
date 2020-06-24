package org.marketdesignresearch.mechlib.core.allocationlimits.utils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.allocationlimits.AllocationLimit;
import org.marketdesignresearch.mechlib.core.allocationlimits.BundleSizeAndGoodAllocationLimit;

public class BundleSizeAndGoodAllocationLimitHelper implements AllocationLimitHelper<BundleSizeAndGoodAllocationLimit>{

	private BundleSizeAllocationLimitHelper bundleSizeHelper = new BundleSizeAllocationLimitHelper();
	private GoodAllocationLimitHelper goodHelper = new GoodAllocationLimitHelper();
	
	@Override
	public boolean validate(BundleSizeAndGoodAllocationLimit allocationLimit, Bundle bundle) {
		return bundleSizeHelper.validate(allocationLimit, bundle)  && goodHelper.validate(allocationLimit, bundle);
	}

	@Override
	public int calculateAllocationBundleSpace(BundleSizeAndGoodAllocationLimit allocationLimit,
			List<? extends Good> startingSpace) {
		List<? extends Good> allocatableGoods = new ArrayList<>(startingSpace);
		allocatableGoods.retainAll(allocationLimit.getGoodAllocationLimit());
		return bundleSizeHelper.calculateAllocationBundleSpace(allocationLimit, allocatableGoods);
	}

	@Override
	public Class<? extends AllocationLimit> getAllocationLimitType() {
		return BundleSizeAndGoodAllocationLimit.class;
	}
	
}
