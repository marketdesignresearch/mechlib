package org.marketdesignresearch.mechlib.core.bundlesampling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.allocationlimits.AllocationLimit;
import org.marketdesignresearch.mechlib.core.allocationlimits.BundleSizeAllocationLimit;
import org.marketdesignresearch.mechlib.core.allocationlimits.BundleSizeAndGoodAllocationLimit;
import org.marketdesignresearch.mechlib.core.allocationlimits.GoodAllocationLimit;
import org.marketdesignresearch.mechlib.core.allocationlimits.NoAllocationLimit;

public class UniformRandomAllocationLimitSampling implements BundleSampling{
	
	private final AllocationLimit allocationLimit;
	private final Random random;
	
	public UniformRandomAllocationLimitSampling(AllocationLimit limit) {
		this(limit, new Random());
	}
	
	public UniformRandomAllocationLimitSampling(AllocationLimit limit, Random random) {
		this.allocationLimit = limit;
		this.random = random;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Bundle getSingleBundle(List<? extends Good> goods) {
		return samplers.get(allocationLimit.getType()).getSingleBundle(allocationLimit, goods, random);
	}
	
	private static interface AllocationLimitSampler<T extends AllocationLimit> {
		Bundle getSingleBundle(T limit, List<? extends Good> goods, Random random);
	}
	
	@SuppressWarnings("rawtypes")
	static Map<Class<? extends AllocationLimit>, AllocationLimitSampler> samplers = new HashMap<>();
	
	static {
		samplers.put(NoAllocationLimit.class, new AllocationLimitSampler<NoAllocationLimit>() {
			@Override
			public Bundle getSingleBundle(NoAllocationLimit limit, List<? extends Good> goods, Random random) {
				return new UniformRandomBundleSampling(random).getSingleBundle(goods);
			}
		});
		samplers.put(BundleSizeAllocationLimit.class, new AllocationLimitSampler<BundleSizeAllocationLimit>() {
			@Override
			public Bundle getSingleBundle(BundleSizeAllocationLimit limit, List<? extends Good> goods, Random random) {
				return new LimitedSizeRandomBundleSampling(limit.getBundleSizeLimit(),random).getSingleBundle(goods);
			}
		});
		samplers.put(GoodAllocationLimit.class, new AllocationLimitSampler<GoodAllocationLimit>() {
			@Override
			public Bundle getSingleBundle(GoodAllocationLimit limit, List<? extends Good> goods, Random random) {
				List<? extends Good> limitedGoods = new ArrayList<>(limit.getGoodAllocationLimit());
				limitedGoods.retainAll(goods);
				return new UniformRandomBundleSampling(random).getSingleBundle(limitedGoods);
			}
		});
		samplers.put(BundleSizeAndGoodAllocationLimit.class, new AllocationLimitSampler<BundleSizeAndGoodAllocationLimit>() {
			@Override
			public Bundle getSingleBundle(BundleSizeAndGoodAllocationLimit limit, List<? extends Good> goods, Random random) {
				List<? extends Good> limitedGoods = new ArrayList<>(limit.getGoodAllocationLimit());
				limitedGoods.retainAll(goods);
				return new LimitedSizeRandomBundleSampling(limit.getBundleSizeLimit(),random).getSingleBundle(goods);
			}
		});
	}
}
