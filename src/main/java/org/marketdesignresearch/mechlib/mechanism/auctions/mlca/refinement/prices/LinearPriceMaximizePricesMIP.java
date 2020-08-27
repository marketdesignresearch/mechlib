package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.refinement.prices;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.Domain;

import edu.harvard.econcs.jopt.solver.SolveParam;
import edu.harvard.econcs.jopt.solver.mip.MIPWrapper;
import edu.harvard.econcs.jopt.solver.mip.Variable;

public class LinearPriceMaximizePricesMIP extends LinearPriceMIP {

	public LinearPriceMaximizePricesMIP(Domain domain, List<UUID> bidders, Allocation allocation,
			PriceConstraints constraint, double timelimit) {
		super(domain, bidders, allocation, constraint, timelimit);
	}

	@Override
	protected MIPWrapper createMIP() {
		MIPWrapper mipWrapper = MIPWrapper.makeNewMaxMIP();

		for (Variable priceVar : this.getPriceVariables().values()) {
			mipWrapper.addObjectiveTerm(1, priceVar);
		}
		mipWrapper.setSolveParam(SolveParam.MARKOWITZ_TOLERANCE, 0.2);
		mipWrapper.setSolveParam(SolveParam.LP_OPTIMIZATION_ALG, 1);
		mipWrapper.setSolveParam(SolveParam.CONSTRAINT_BACKOFF_LIMIT, 0);
		return mipWrapper;
	}

	public BigDecimal getPriceSum() {
		return this.getPrices().getPriceMap().entrySet().stream()
				.map(e -> e.getValue().getAmount().multiply(BigDecimal.valueOf(e.getKey().getQuantity())))
				.reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
	}

	@Override
	protected String getMIPName() {
		return "max-prices";
	}
}
