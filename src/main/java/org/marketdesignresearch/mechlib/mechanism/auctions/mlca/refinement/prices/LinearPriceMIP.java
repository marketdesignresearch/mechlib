package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.refinement.prices;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.FileSystems;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.price.LinearPrices;
import org.marketdesignresearch.mechlib.core.price.Price;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentation;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentation.MipPurpose;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentationable;
import org.marketdesignresearch.mechlib.utils.CPLEXUtils;

import edu.harvard.econcs.jopt.solver.IMIPResult;
import edu.harvard.econcs.jopt.solver.ISolution;
import edu.harvard.econcs.jopt.solver.SolveParam;
import edu.harvard.econcs.jopt.solver.mip.Constraint;
import edu.harvard.econcs.jopt.solver.mip.MIP;
import edu.harvard.econcs.jopt.solver.mip.MIPWrapper;
import edu.harvard.econcs.jopt.solver.mip.VarType;
import edu.harvard.econcs.jopt.solver.mip.Variable;
import edu.harvard.econcs.jopt.solver.server.cplex.CPlexMIPSolver;
import lombok.AccessLevel;
import lombok.Getter;

public abstract class LinearPriceMIP implements MipInstrumentationable {

	private MipInstrumentation instrumentation = MipInstrumentation.NO_OP;
	@Getter(AccessLevel.PROTECTED)
	private Map<Good, Variable> priceVariables;
	private MIPWrapper mip;

	@Getter(AccessLevel.PROTECTED)
	private Allocation allocation;
	@Getter(AccessLevel.PROTECTED)
	private Set<Bidder> bidders;
	private PriceConstraints priceConstraint;
	private double timelimit;

	private LinearPrices prices;

	public LinearPriceMIP(Domain domain, List<UUID> bidders, Allocation allocation, PriceConstraints constraint,
			double timelimit) {
		this.bidders = bidders.stream()
				.map(id -> domain.getBidders().stream().filter(b -> b.getId().equals(id)).findAny().orElseThrow())
				.collect(Collectors.toCollection(LinkedHashSet::new));
		this.allocation = allocation;
		this.timelimit = timelimit;
		this.priceConstraint = constraint;
		this.createVariables(domain);
	}

	private void initMIP() {
		this.mip = this.createMIP();
		this.priceVariables.values().forEach(v -> this.mip.add(v));
		this.addPriceConstraints();
	}

	private void addPriceConstraints() {
		Constraint c;
		for (Bidder b : this.bidders) {
			Bundle allocated = this.getBidderAllocation(b.getId());

			for (Bundle bid : this.priceConstraint.getConstrainedBids(b.getId())) {
				c = getMIP().beginNewLEQConstraint(this.priceConstraint.getRightHandSide(b.getId(), bid).doubleValue());
				this.addPriceVariables(c, allocated, bid);
				getMIP().add(c);
			}
		}
	}

	private void createVariables(Domain domain) {
		// Create Variables;
		priceVariables = new LinkedHashMap<>();
		int nr = 0;
		for (Good g : domain.getGoods()) {
			Variable var = new Variable("Price for Good " + (++nr), VarType.DOUBLE, 0, MIP.MAX_VALUE);
			// unallocated bundle
			if (allocation.getAllocatedBundle().countGood(g) != g.getQuantity()) {
				var.setUpperBound(0);
			}
			priceVariables.put(g, var);
		}
	}

	protected abstract MIPWrapper createMIP();

	protected MIPWrapper getMIP() {
		if (this.mip == null) {
			this.initMIP();
		}
		return this.mip;
	}

	@Override
	public void setMipInstrumentation(MipInstrumentation mipInstrumentation) {
		this.instrumentation = mipInstrumentation;
	}

	public LinearPrices getPrices() {
		try {
			if (this.prices == null) {
				this.prices = this.solveMIP();
			}
			return prices;
		} catch (RuntimeException e) {
			//new CPlexMIPSolver().exportToDisk(this.mip, FileSystems.getDefault().getPath("mip",
			//		this.getMIPName() + "-" + System.currentTimeMillis() + ".lp"));
			throw e;
		}
	}

	protected LinearPrices solveMIP() {
		if (timelimit > 0)
			getMIP().setSolveParam(SolveParam.TIME_LIMIT, timelimit);
		getMIP().setSolveParam(SolveParam.CALCULATE_CONFLICT_SET, false);
		this.instrumentation.preMIP(MipPurpose.REFINEMENT_PRICES.name(), getMIP());
		IMIPResult result = CPLEXUtils.SOLVER.solve(getMIP());
		this.instrumentation.postMIP(MipPurpose.REFINEMENT_PRICES.name(), getMIP(), result);
		return this.adaptMIPResult(result);
	}

	protected LinearPrices adaptMIPResult(ISolution result) {
		Map<Good, Price> prices = new LinkedHashMap<>();
		for (Map.Entry<Good, Variable> entry : this.priceVariables.entrySet()) {
			prices.put(entry.getKey(),
					new Price(BigDecimal.valueOf(result.getValue(entry.getValue())).setScale(6, RoundingMode.CEILING)));
		}
		return new LinearPrices(prices);
	}

	protected void addPriceVariables(Constraint c, Bundle allocated, Bundle x) {
		for (Map.Entry<Good, Variable> entry : priceVariables.entrySet()) {
			if (allocated.contains(entry.getKey())) {
				c.addTerm(allocated.countGood(entry.getKey()), entry.getValue());
			}
			if (x.contains(entry.getKey())) {
				c.addTerm(-x.countGood(entry.getKey()), entry.getValue());
			}
		}
	}

	protected Bundle getBidderAllocation(UUID bidder) {
		return this.getAllocation().getTradesMap().entrySet().stream().filter(b -> b.getKey().getId().equals(bidder))
				.map(b -> b.getValue().getBundle()).findFirst().orElse(Bundle.EMPTY);
	}

	protected abstract String getMIPName();
}
