package org.marketdesignresearch.mechlib.winnerdetermination.allocationlimit;

import java.util.List;
import java.util.Map;

import org.marketdesignresearch.mechlib.core.allocationlimits.AllocationLimit;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValuePair;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.springframework.data.util.Pair;

import edu.harvard.econcs.jopt.solver.mip.Constraint;
import edu.harvard.econcs.jopt.solver.mip.Variable;

public interface OrWDPAllocationLimitProcessor<T extends AllocationLimit> {
	Pair<List<Constraint>, List<Variable>> createVariablesAndConstraints(Bidder bidder, BundleValueBid<?> bid, T limit, Map<BundleExactValuePair, Variable> bidVariables);
	Class<T> getType();
}
