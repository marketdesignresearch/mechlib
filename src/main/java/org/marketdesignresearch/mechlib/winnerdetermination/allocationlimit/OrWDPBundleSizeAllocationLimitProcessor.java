package org.marketdesignresearch.mechlib.winnerdetermination.allocationlimit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.marketdesignresearch.mechlib.core.allocationlimits.BundleSizeAllocationLimit;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValuePair;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.springframework.data.util.Pair;

import edu.harvard.econcs.jopt.solver.mip.CompareType;
import edu.harvard.econcs.jopt.solver.mip.Constraint;
import edu.harvard.econcs.jopt.solver.mip.VarType;
import edu.harvard.econcs.jopt.solver.mip.Variable;

public class OrWDPBundleSizeAllocationLimitProcessor implements OrWDPAllocationLimitProcessor<BundleSizeAllocationLimit>{

	@Override
	public Pair<List<Constraint>, List<Variable>> createVariablesAndConstraints(Bidder bidder, BundleValueBid<?> bid,
			BundleSizeAllocationLimit limit, Map<BundleExactValuePair, Variable> bidVariables) {
		
		List<Constraint> constraints = new ArrayList<>();
		List<Variable> goodCount = new ArrayList<>();
		
		for(BundleExactValuePair pair : bid.getBundleBids()) {
			Constraint c = new Constraint(CompareType.EQ, 0);
			c.addTerm(- pair.getBundle().getTotalAmount(), bidVariables.get(pair));
			Variable gVar = new Variable("Counter for:"+pair, VarType.INT, 0, pair.getBundle().getTotalAmount());
			c.addTerm(1,gVar);
			goodCount.add(gVar);
			constraints.add(c);
		}
		
		Constraint limitC = new Constraint(CompareType.LEQ, limit.getBundleSizeLimit());
		goodCount.forEach(v -> limitC.addTerm(1,v));
		constraints.add(limitC);
		
		return Pair.of(constraints,goodCount);
	}

	@Override
	public Class<BundleSizeAllocationLimit> getType() {
		return BundleSizeAllocationLimit.class;
	}
}
