package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.refinement.prices;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValuePair;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.price.LinearPrices;

import edu.harvard.econcs.jopt.solver.ISolution;
import edu.harvard.econcs.jopt.solver.SolveParam;
import edu.harvard.econcs.jopt.solver.mip.Constraint;
import edu.harvard.econcs.jopt.solver.mip.MIPWrapper;
import edu.harvard.econcs.jopt.solver.mip.Variable;
import lombok.Getter;

public class LinearPriceMinimizeDeltaMIP extends LinearPriceMIP {

	// max delta of all not yet fixed bundles
	private Variable delta;
	private BundleExactValueBids bids;
	@Getter
	private BigDecimal deltaResult;

	public LinearPriceMinimizeDeltaMIP(Domain domain, List<UUID> bidders, BundleExactValueBids bids, Allocation allocation, PriceConstraints constraint) {
		super(domain, bidders, allocation, constraint);
		this.bids = bids;
	}

	@Override
	protected MIPWrapper createMIP() {
		MIPWrapper mipWrapper = MIPWrapper.makeNewMinMIP();	    	
		delta = mipWrapper.makeNewDoubleVar("Delta");
		
		//Objective
		mipWrapper.addObjectiveTerm(1, delta);
    	
		// Constraints
		Constraint c;
		for(Bidder bidder: this.getBidders()) {
			BundleExactValueBid values = bids.getBid(bidder);
			
			Bundle allocated = this.getAllocation().allocationOf(bidder).getBundle();
			BigDecimal allocatedValue = values.getBidForBundle(allocated).getAmount();
	
			for(BundleExactValuePair bid : values.getBundleBids()) {
				// do not restrict the allocated bundle
				if(!bid.getBundle().equals(allocated)) {
					BigDecimal value = allocatedValue.subtract(bid.getAmount());
					c = mipWrapper.beginNewLEQConstraint(value.doubleValue());
					this.addPriceVariables(c, allocated, bid.getBundle());
					c.addTerm(-1,delta);

					mipWrapper.add(c);
				}
			}
		}
		
		mipWrapper.setSolveParam(SolveParam.ABSOLUTE_VAR_BOUND_GAP, 1e-9d);
		// TODO Timelimit?
		mipWrapper.setSolveParam(SolveParam.LP_OPTIMIZATION_ALG, 1);
		
    	return mipWrapper;
	}

	@Override
	protected LinearPrices adaptMIPResult(ISolution result) {
		deltaResult = BigDecimal.valueOf(result.getValue(this.delta));
		return super.adaptMIPResult(result);
	}
}
