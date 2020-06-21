package org.marketdesignresearch.mechlib.core.allocationlimits.validators;

import java.util.List;

import org.apache.commons.math3.util.CombinatoricsUtils;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.allocationlimits.AllocationLimit;
import org.marketdesignresearch.mechlib.core.allocationlimits.BundleSizeAllocationLimit;

public class BundleSizeAllocationLimitHelper implements AllocationLimitHelper<BundleSizeAllocationLimit> {

	@Override
	public boolean validate(BundleSizeAllocationLimit allocationLimit, Bundle bundle) {
		return bundle.getTotalAmount() <= allocationLimit.getBundleSizeLimit();
	}

	@Override
	public Class<? extends AllocationLimit> getAllocationLimitType() {
		return BundleSizeAllocationLimit.class;
	}

	@Override
	public int calculateAllocationBundleSpace(BundleSizeAllocationLimit allocationLimit,
			List<? extends Good> startingSpace) {
		int numberOfItems = startingSpace.stream().mapToInt(Good::getQuantity).sum();
		int maxNumberOfBundlesInterestedIn = 0;
    	for(int i = 0; i <= allocationLimit.getBundleSizeLimit(); i++) {
    		maxNumberOfBundlesInterestedIn += CombinatoricsUtils.binomialCoefficient(numberOfItems, i);
    	}
    	return maxNumberOfBundlesInterestedIn;
	}
	
}
