package org.marketdesignresearch.mechlib.core.allocationlimits;

import java.util.List;
import java.util.Random;

import org.apache.commons.math3.util.CombinatoricsUtils;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.bundlesampling.LimitedSizeRandomBundleSampling;

import com.google.common.base.Preconditions;

import edu.harvard.econcs.jopt.solver.mip.CompareType;
import lombok.Getter;

/**
 * TODO add documentation
 * 
 * @author Manuel
 *
 */
public class BundleSizeAllocationLimit extends AllocationLimit {
	@Getter
	private final int bundleSizeLimit;
	@Getter
	private final List<? extends Good> goods;

	public BundleSizeAllocationLimit(int bundleSizeLimit, List<? extends Good> goods) {
		super(goods);
		this.bundleSizeLimit = bundleSizeLimit;
		this.goods = goods;
		AllocationLimitConstraint constraint = new AllocationLimitConstraint(CompareType.LEQ, bundleSizeLimit);
		for (Good g : goods) {
			constraint.addTerm(1, g);
		}
		this.addAllocationLimitConstraint(constraint);
	}

	@Override
	public Bundle getUniformRandomBundle(Random random, List<? extends Good> goods) {
		Preconditions.checkArgument(this.validateDomainCompatiblity(goods));
		return new LimitedSizeRandomBundleSampling(this.bundleSizeLimit, random).getSingleBundle(goods);
	}

	@Override
	public int calculateAllocationBundleSpace(List<? extends Good> startingSpace) {
		Preconditions.checkArgument(this.validateDomainCompatiblity(goods));
		int numberOfItems = startingSpace.stream().mapToInt(Good::getQuantity).sum();
		int maxNumberOfBundlesInterestedIn = 0;
		for (int i = 0; i <= this.getBundleSizeLimit(); i++) {
			maxNumberOfBundlesInterestedIn += CombinatoricsUtils.binomialCoefficient(numberOfItems, i);
		}
		return maxNumberOfBundlesInterestedIn;
	}
}
