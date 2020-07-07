package org.marketdesignresearch.mechlib.core.allocationlimits;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.bundlesampling.UniformRandomBundleSampling;

import lombok.Getter;

/**
 * TODO add documentation
 * 
 * @author Manuel
 *
 */
public class AllocationLimit {
	
	@Getter
	private List<AllocationLimitConstraint> constraints = new ArrayList<>();
	
	public static AllocationLimit NO = new AllocationLimit();
	
	protected void addAllocationLimitConstraint(AllocationLimitConstraint constraint) {
		this.constraints.add(constraint);
	}
	
	public int calculateAllocationBundleSpace(List<? extends Good> startingSpace) {
		return (int) Math.pow(2, startingSpace.stream().mapToInt(Good::getQuantity).sum());
	}
	
	public Bundle getUniformRandomBundle(Random random, List<? extends Good> goods) {
		return new UniformRandomBundleSampling(random).getSingleBundle(goods);
	}
	
	public boolean validate(Bundle bundle) {
		return this.getConstraints().stream().map(c -> c.validateConstraint(bundle)).reduce(true, Boolean::logicalAnd).booleanValue();
	}
	
	public boolean validateDomainCompatiblity(List<? extends Good> domainGoods) {
		return true;
	}
}
