package org.marketdesignresearch.mechlib.core.allocationlimits;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.allocationlimits.AllocationLimitConstraint.Type;
import org.marketdesignresearch.mechlib.core.bundlesampling.UniformRandomBundleSampling;

import com.google.common.base.Preconditions;

import lombok.Getter;

/**
 * TODO add documentation
 * 
 * @author Manuel
 *
 */
public class GoodAllocationLimit extends AllocationLimit {
	@Getter
	private final List<? extends Good> domainGoods;
	@Getter
	private final List<? extends Good> limitedGoods;

	public GoodAllocationLimit(List<? extends Good> domainGoods, List<? extends Good> limitedGoods) {
		Preconditions.checkArgument(domainGoods.containsAll(limitedGoods));
		this.domainGoods = domainGoods;
		this.limitedGoods = limitedGoods;

		List<Good> excludedGoods = new ArrayList<>(domainGoods);
		excludedGoods.removeAll(limitedGoods);

		AllocationLimitConstraint exGoodConstraint = new AllocationLimitConstraint(Type.EQ, 0);
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

		return new UniformRandomBundleSampling(random).getSingleBundle(goods);
	}

	@Override
	public int calculateAllocationBundleSpace(List<? extends Good> startingSpace) {
		Preconditions.checkArgument(this.validateDomainCompatiblity(startingSpace));

		// Intersection with limited goods
		startingSpace = new ArrayList<>(startingSpace);
		startingSpace.retainAll(this.limitedGoods);

		return (int) Math.pow(2, startingSpace.stream().mapToInt(Good::getQuantity).sum());
	}

	@Override
	public boolean validateDomainCompatiblity(List<? extends Good> domainGoods) {
		return this.domainGoods.containsAll(domainGoods);
	}
}
