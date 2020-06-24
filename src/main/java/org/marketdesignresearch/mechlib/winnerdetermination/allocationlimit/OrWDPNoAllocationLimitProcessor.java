package org.marketdesignresearch.mechlib.winnerdetermination.allocationlimit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.marketdesignresearch.mechlib.core.allocationlimits.NoAllocationLimit;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValuePair;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.springframework.data.util.Pair;

import edu.harvard.econcs.jopt.solver.mip.Constraint;
import edu.harvard.econcs.jopt.solver.mip.Variable;

public class OrWDPNoAllocationLimitProcessor implements OrWDPAllocationLimitProcessor<NoAllocationLimit>{

	@Override
	public Pair<List<Constraint>, List<Variable>> createVariablesAndConstraints(Bidder bidder, BundleValueBid<?> bid,
			NoAllocationLimit limit, Map<BundleExactValuePair, Variable> bidVariables) {
		return Pair.of(new ArrayList<Constraint>(), new ArrayList<Variable>());
	}

	@Override
	public Class<NoAllocationLimit> getType() {
		return NoAllocationLimit.class;
	}

}
