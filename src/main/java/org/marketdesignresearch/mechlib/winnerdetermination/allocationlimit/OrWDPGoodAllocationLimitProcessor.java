package org.marketdesignresearch.mechlib.winnerdetermination.allocationlimit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.marketdesignresearch.mechlib.core.allocationlimits.GoodAllocationLimit;
import org.marketdesignresearch.mechlib.core.allocationlimits.utils.GoodAllocationLimitHelper;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValuePair;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.springframework.data.util.Pair;

import edu.harvard.econcs.jopt.solver.mip.CompareType;
import edu.harvard.econcs.jopt.solver.mip.Constraint;
import edu.harvard.econcs.jopt.solver.mip.Variable;

public class OrWDPGoodAllocationLimitProcessor implements OrWDPAllocationLimitProcessor<GoodAllocationLimit>{

	@Override
	public Pair<List<Constraint>, List<Variable>> createVariablesAndConstraints(Bidder bidder, BundleValueBid<?> bid,
			GoodAllocationLimit limit, Map<BundleExactValuePair, Variable> bidVariables) {
		List<Constraint> constraints = new ArrayList<>();
		List<Variable> variables = new ArrayList<>();
		
		GoodAllocationLimitHelper helper = new GoodAllocationLimitHelper();
		
		for(BundleExactValuePair pair : bid.getBundleBids()) {
			if(!helper.validate(limit, pair.getBundle())) {
				Constraint c = new Constraint(CompareType.EQ, 0);
				c.addTerm(1,bidVariables.get(pair));
				constraints.add(c);
			}
		}
		return Pair.of(constraints,variables);
	}

	@Override
	public Class<GoodAllocationLimit> getType() {
		return GoodAllocationLimit.class;
	}

}
