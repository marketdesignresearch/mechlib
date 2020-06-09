package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.refinement.prices;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.Domain;

import edu.harvard.econcs.jopt.solver.mip.MIPWrapper;
import edu.harvard.econcs.jopt.solver.mip.Variable;

public class LinearPriceMaximizePricesMIP extends LinearPriceMIP{
	
	public LinearPriceMaximizePricesMIP(Domain domain, List<UUID> bidders, Allocation allocation,
			PriceConstraints constraint) {
		super(domain, bidders, allocation, constraint);
	}

	@Override
	protected MIPWrapper createMIP() {
		MIPWrapper mipWrapper = MIPWrapper.makeNewMaxMIP();	   
		
		for(Variable priceVar : this.getPriceVariables().values()) {
			mipWrapper.addObjectiveTerm(1, priceVar);
		}
		
    	return mipWrapper;
	}
	
	public BigDecimal getPriceSum() {
		return this.getPrices().getPriceMap().entrySet().stream().map(e -> e.getValue().getAmount().multiply(BigDecimal.valueOf(e.getKey().getQuantity()))).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
	}
}
