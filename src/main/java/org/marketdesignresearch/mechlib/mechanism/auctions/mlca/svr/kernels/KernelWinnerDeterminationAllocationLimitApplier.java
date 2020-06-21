package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr.kernels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.allocationlimits.AllocationLimit;
import org.marketdesignresearch.mechlib.core.allocationlimits.BundleSizeAllocationLimit;
import org.marketdesignresearch.mechlib.core.allocationlimits.GoodAllocationLimit;
import org.marketdesignresearch.mechlib.core.allocationlimits.NoAllocationLimit;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;

import edu.harvard.econcs.jopt.solver.mip.CompareType;
import edu.harvard.econcs.jopt.solver.mip.Constraint;
import edu.harvard.econcs.jopt.solver.mip.Variable;

public enum KernelWinnerDeterminationAllocationLimitApplier {
	APPLIER;
	
	@SuppressWarnings("rawtypes")
	private Map<Class<? extends AllocationLimit>, KernelAllocationLimitApplier> appliers = new HashMap<>();
	
	@SuppressWarnings("unchecked")
	public List<Constraint> apply(Bidder b, WinnerDeterminationWithExcludedBundles wdp) {
		return this.appliers.get(b.getAllocationLimit().getType()).apply(b.getAllocationLimit(), b, wdp);
	}
	
	private KernelWinnerDeterminationAllocationLimitApplier() {
		appliers.put(NoAllocationLimit.class, new KernelAllocationLimitApplier<NoAllocationLimit>() {
			@Override
			public List<Constraint> apply(NoAllocationLimit limit, Bidder b,
					WinnerDeterminationWithExcludedBundles wdp) {
				return new ArrayList<>();
			}
		});
		appliers.put(BundleSizeAllocationLimit.class, new KernelAllocationLimitApplier<BundleSizeAllocationLimit>() {
			@Override
			public List<Constraint> apply(BundleSizeAllocationLimit limit, Bidder b,
					WinnerDeterminationWithExcludedBundles wdp) {
				Constraint constraint = new Constraint(CompareType.LEQ, limit.getBundleSizeLimit());
				for(Map.Entry<Good, Variable> entry : wdp.getBidderGoodVariables().get(b.getId()).entrySet()) {
					if(entry.getKey().getQuantity() > 1)
						throw new IllegalStateException("BundleSizeAllocationLimit for generic worlds not implemented!");
					constraint.addTerm(1, entry.getValue());
				}
				return List.of(constraint);
			}
		});
		appliers.put(GoodAllocationLimit.class, new KernelAllocationLimitApplier<GoodAllocationLimit>() {
			@Override
			public List<Constraint> apply(GoodAllocationLimit limit, Bidder b,
					WinnerDeterminationWithExcludedBundles wdp) {
				List<Constraint> constraints = new ArrayList<>();
				for(Map.Entry<Good, Variable> entry : wdp.getBidderGoodVariables().get(b.getId()).entrySet()) {
					if(!limit.getGoodAllocationLimit().contains(entry.getKey())) {
						Constraint c = new Constraint(CompareType.EQ, 0);
						c.addTerm(1,entry.getValue());
						constraints.add(c);
					}
				}
				return constraints;
			}
		});
	}
	
	private static interface KernelAllocationLimitApplier<T extends AllocationLimit> {
		List<Constraint> apply(T limit, Bidder b, WinnerDeterminationWithExcludedBundles wdp);
	}
}
