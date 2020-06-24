package org.marketdesignresearch.mechlib.winnerdetermination.allocationlimit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.marketdesignresearch.mechlib.core.allocationlimits.AllocationLimit;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValuePair;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.springframework.data.util.Pair;

import edu.harvard.econcs.jopt.solver.mip.Constraint;
import edu.harvard.econcs.jopt.solver.mip.Variable;

public enum OrWDPAllocationLimitUtils {
	PROCESSOR;
	
	@SuppressWarnings("rawtypes")
	private Map<Class<? extends AllocationLimit>, OrWDPAllocationLimitProcessor> processor = new HashMap<>();
	
	private OrWDPAllocationLimitUtils() {
		this.addProcessor(new OrWDPNoAllocationLimitProcessor());
		this.addProcessor(new OrWDPGoodAllocationLimitProcessor());
		this.addProcessor(new OrWDPBundleSizeAllocationLimitProcessor());
		this.addProcessor(new OrWDPBundleSizeAndGoodAllocationLimitProcessor());
	}
	
	public void addProcessor(OrWDPAllocationLimitProcessor<?> proc) {
		this.processor.put(proc.getType(), proc);
	}
	
	@SuppressWarnings("unchecked")
	public Pair<List<Constraint>, List<Variable>> createVariablesAndConstraints(Bidder bidder, BundleValueBid<?> bid, Map<BundleExactValuePair, Variable> bidVariables) {
		return processor.get(bidder.getAllocationLimit().getType()).createVariablesAndConstraints(bidder, bid, bidder.getAllocationLimit(), bidVariables);
	}
}
