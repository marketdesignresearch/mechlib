package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.refinement.prices;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import edu.harvard.econcs.jopt.solver.mip.LinearTerm;
import edu.harvard.econcs.jopt.solver.mip.MIPWrapper;
import edu.harvard.econcs.jopt.solver.mip.Variable;
import lombok.Getter;

public class LinearPriceMinimizeNumberOfPositiveDeltasMIP extends LinearPriceMIP{

	private Map<Bidder,Map<Bundle,Variable>> zVariables = new LinkedHashMap<>();
	private BundleExactValueBids bids;
	private BigDecimal maxDelta;
	private BigDecimal zeta;
	
	@Getter
	private Map<Bidder,Set<Bundle>> positiveDeltas = new LinkedHashMap<>();

	public LinearPriceMinimizeNumberOfPositiveDeltasMIP(Domain domain, List<UUID> bidders, BundleExactValueBids bids, Allocation allocation, PriceConstraints constraint, BigDecimal maxDelta, BigDecimal offset) {
		super(domain, bidders, allocation, constraint);
		this.bids = bids;
		this.maxDelta = maxDelta.add(offset);
		this.zeta = maxDelta.add(offset.multiply(BigDecimal.valueOf(2)));
	}

	@Override
	protected MIPWrapper createMIP() {

		MIPWrapper mipWrapper = MIPWrapper.makeNewMaxMIP();	    	
    	
		//Constraints
		Constraint constraint;
		for(Bidder bidder: this.getBidders()) {
			
			this.zVariables.put(bidder, new LinkedHashMap<>());
			
			BundleExactValueBid values = bids.getBid(bidder);
			
			Bundle allocated = this.getAllocation().allocationOf(bidder).getBundle();
			BigDecimal allocatedValue = values.getBidForBundle(allocated).getAmount();
			
			for(BundleExactValuePair bid : bids.getBid(bidder).getBundleBids()) {
				// do not consider allocated bundle as the delta of this bundle is 0 by definition
				if(!bid.getBundle().equals(allocated)) {

					BigDecimal allocatedMinusBundleValue = allocatedValue.subtract(bid.getAmount());
					constraint = mipWrapper.beginNewLEQConstraint(allocatedMinusBundleValue.add(maxDelta).doubleValue());
					this.addPriceVariables(constraint, allocated, bid.getBundle());
				
					Variable zVariable = mipWrapper.makeNewBooleanVar("Delta "+bidder.getId()+ " "+bid.getBundle().toString());
					LinearTerm lt = new LinearTerm(1, zVariable);
					mipWrapper.addObjectiveTerm(lt);

					constraint.addTerm(zeta.doubleValue(),zVariable);

					mipWrapper.add(constraint);
					this.zVariables.get(bidder).put(bid.getBundle(), zVariable);
				}
			}
		}
		
		mipWrapper.setSolveParam(SolveParam.ABSOLUTE_VAR_BOUND_GAP, 1e-9d);
		// TODO Timelimit?
		mipWrapper.setSolveParam(SolveParam.LP_OPTIMIZATION_ALG, 3);
		
		return mipWrapper;
	}
	
	@Override
	protected LinearPrices adaptMIPResult(ISolution result) {
		for(Bidder bidder: this.getBidders()) {
			Set<Bundle> pDelta = new HashSet<>();
			this.positiveDeltas.put(bidder, pDelta);
			for(Map.Entry<Bundle,Variable> var : this.zVariables.get(bidder).entrySet()) {
				if(result.getValue(var.getValue()) == 0) {
					pDelta.add(var.getKey());
				}
			}
		}
		
		return super.adaptMIPResult(result);
	}

}
