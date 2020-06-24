package org.marketdesignresearch.mechlib.winnerdetermination.allocationlimit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.marketdesignresearch.mechlib.core.allocationlimits.BundleSizeAndGoodAllocationLimit;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValuePair;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.springframework.data.util.Pair;

import edu.harvard.econcs.jopt.solver.mip.Constraint;
import edu.harvard.econcs.jopt.solver.mip.Variable;

public class OrWDPBundleSizeAndGoodAllocationLimitProcessor implements OrWDPAllocationLimitProcessor<BundleSizeAndGoodAllocationLimit>{

	private OrWDPBundleSizeAllocationLimitProcessor bundleSizeProcessor = new OrWDPBundleSizeAllocationLimitProcessor();
	private OrWDPGoodAllocationLimitProcessor goodLimitProcessor = new OrWDPGoodAllocationLimitProcessor();
	
	@Override
	public Pair<List<Constraint>, List<Variable>> createVariablesAndConstraints(Bidder bidder, BundleValueBid<?> bid,
			BundleSizeAndGoodAllocationLimit limit, Map<BundleExactValuePair, Variable> bidVariables) {
		Pair<List<Constraint>, List<Variable>> bundleSize = bundleSizeProcessor.createVariablesAndConstraints(bidder, bid, limit, bidVariables);
		Pair<List<Constraint>, List<Variable>> goodLimit = goodLimitProcessor.createVariablesAndConstraints(bidder, bid, limit, bidVariables);
		
		List<Constraint> constraints = new ArrayList<>();
		constraints.addAll(bundleSize.getFirst());
		constraints.addAll(goodLimit.getFirst());
		
		List<Variable> variables = new ArrayList<>();
		variables.addAll(bundleSize.getSecond());
		variables.addAll(goodLimit.getSecond());
		
		return Pair.of(constraints, variables);
	}

	@Override
	public Class<BundleSizeAndGoodAllocationLimit> getType() {
		return BundleSizeAndGoodAllocationLimit.class;
	}

}
