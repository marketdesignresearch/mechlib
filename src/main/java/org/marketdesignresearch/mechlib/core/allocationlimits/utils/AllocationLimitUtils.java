package org.marketdesignresearch.mechlib.core.allocationlimits.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.allocationlimits.AllocationLimit;

public enum AllocationLimitUtils {
	HELPER;
	
	@SuppressWarnings("rawtypes")
	private Map<Class<? extends AllocationLimit>, AllocationLimitHelper> helper = new HashMap<>();
	
	private AllocationLimitUtils() {
		this.addAllocationValidator(new NoAllocationLimitHelper());
		this.addAllocationValidator(new BundleSizeAllocationLimitHelper());
		this.addAllocationValidator(new GoodAllocationLimitHelper());
	}
	
	private void addAllocationValidator(AllocationLimitHelper<?> validator) {
		this.helper.put(validator.getAllocationLimitType(), validator);
	}
	
	@SuppressWarnings("unchecked")
	public boolean validate(AllocationLimit limit, Bundle bundle) {
		return helper.get(limit.getType()).validate(limit, bundle);
	}
	
	@SuppressWarnings("unchecked")
	public int calculateAllocationBundleSpaceSize(AllocationLimit limit, List<? extends Good> startingSpace) {
		return helper.get(limit.getType()).calculateAllocationBundleSpace(limit, startingSpace);
	}
}
