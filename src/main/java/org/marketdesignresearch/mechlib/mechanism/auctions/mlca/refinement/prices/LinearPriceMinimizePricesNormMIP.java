package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.refinement.prices;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.core.Good;

import edu.harvard.econcs.jopt.solver.SolveParam;
import edu.harvard.econcs.jopt.solver.mip.Constraint;
import edu.harvard.econcs.jopt.solver.mip.MIPWrapper;
import edu.harvard.econcs.jopt.solver.mip.QuadraticTerm;
import edu.harvard.econcs.jopt.solver.mip.Variable;

public class LinearPriceMinimizePricesNormMIP extends LinearPriceMIP {

	private BigDecimal priceSum;
	
	public LinearPriceMinimizePricesNormMIP(Domain domain, List<UUID> bidders, Allocation allocation,
			PriceConstraints constraint, BigDecimal priceSum) {
		super(domain, bidders, allocation, constraint);
		
		this.priceSum = priceSum;
	}

	@Override
	protected MIPWrapper createMIP() {
		MIPWrapper mipWrapper = MIPWrapper.makeNewMinMIP();	  
		Constraint constraint = mipWrapper.beginNewGEQConstraint(priceSum.doubleValue() - 0.1);
		
		// Create Variables and Objective
		for(Map.Entry<Good, Variable> entry : this.getPriceVariables().entrySet()) {
			constraint.addTerm(entry.getKey().getQuantity(),entry.getValue());
			mipWrapper.addObjectiveTerm(new QuadraticTerm(1, entry.getValue(), entry.getValue()));
		}
		
		mipWrapper.add(constraint);
		return mipWrapper;
	}
	
}
