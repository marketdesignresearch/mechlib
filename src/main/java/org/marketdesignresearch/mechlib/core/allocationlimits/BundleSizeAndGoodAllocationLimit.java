package org.marketdesignresearch.mechlib.core.allocationlimits;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.util.CombinatoricsUtils;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.bundlesampling.LimitedSizeRandomBundleSampling;

import com.google.common.base.Preconditions;

import edu.harvard.econcs.jopt.solver.mip.CompareType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Limit the allocation of this bidder to a limited set of goods and a maximum
 * number of items.
 * 
 * @author Manuel Beyeler
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class BundleSizeAndGoodAllocationLimit extends AllocationLimit {
	@Getter
	private final int bundleSizeLimit;
	@Getter
	private final List<? extends Good> domainGoods;
	@Getter
	private final List<? extends Good> limitedGoods;

	/**
	 * Creates a new BundleSizeAndGoodAllocationLimit
	 * 
	 * @param bundleSizeLimit the maximum number of items allocated to this bidder
	 * @param domainGoods     all goods in this domain
	 * @param limitedGoods    a limited set of goods that may be allocated to this
	 *                        bidder
	 */
	public BundleSizeAndGoodAllocationLimit(int bundleSizeLimit, List<? extends Good> domainGoods,
			List<? extends Good> limitedGoods) {
		super(domainGoods);
		Preconditions.checkArgument(domainGoods.containsAll(limitedGoods));
		this.bundleSizeLimit = bundleSizeLimit;
		this.domainGoods = domainGoods;
		this.limitedGoods = limitedGoods;
		AllocationLimitConstraint constraint = new AllocationLimitConstraint(CompareType.LEQ, bundleSizeLimit);
		for (Good g : this.limitedGoods) {
			constraint.addTerm(1, g);
		}

		this.addAllocationLimitConstraint(constraint);

		List<Good> excludedGoods = new ArrayList<>(domainGoods);
		excludedGoods.removeAll(limitedGoods);

		AllocationLimitConstraint exGoodConstraint = new AllocationLimitConstraint(CompareType.EQ, 0);
		for (Good exGood : excludedGoods) {
			exGoodConstraint.addTerm(1, exGood);
		}
		this.addAllocationLimitConstraint(exGoodConstraint);
	}

	@Override
	public Bundle getUniformRandomBundle(Random random, List<? extends Good> goods) {
		Preconditions.checkArgument(this.validateDomainCompatiblity(goods));

		// Intersection with limited goods
		goods = new ArrayList<>(goods);
		goods.retainAll(this.limitedGoods);

		return new LimitedSizeRandomBundleSampling(this.bundleSizeLimit, random).getSingleBundle(goods);
	}

	@Override
	public int calculateAllocationBundleSpace(List<? extends Good> startingSpace) {
		Preconditions.checkArgument(this.validateDomainCompatiblity(startingSpace));

		int numberOfItems = startingSpace.stream().mapToInt(Good::getQuantity).sum();
		int maxNumberOfBundlesInterestedIn = 0;
		for (int i = 0; i <= this.getBundleSizeLimit(); i++) {
			maxNumberOfBundlesInterestedIn += CombinatoricsUtils.binomialCoefficient(numberOfItems, i);
		}
		return maxNumberOfBundlesInterestedIn;
	}

	@Override
	public boolean validateDomainCompatiblity(List<? extends Good> domainGoods) {
		return this.domainGoods.containsAll(domainGoods);
	}
}
