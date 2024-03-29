package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.refinement.prices;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import edu.harvard.econcs.jopt.solver.mip.CompareType;
import edu.harvard.econcs.jopt.solver.mip.Constraint;
import edu.harvard.econcs.jopt.solver.mip.LinearTerm;
import edu.harvard.econcs.jopt.solver.mip.MIPWrapper;
import edu.harvard.econcs.jopt.solver.mip.QuadraticTerm;
import edu.harvard.econcs.jopt.solver.mip.VarType;
import edu.harvard.econcs.jopt.solver.mip.Variable;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LinearPriceMinimizeDeltasWithNorm extends LinearPriceMIP {

	@Override
	protected LinearPrices solveMIP() {
		try {
			return super.solveMIP();
		} catch (RuntimeException e) {
			// try different CPLEX Parameters
			try {
				// try default CPLEX
				this.getMIP().setSolveParam(SolveParam.LP_OPTIMIZATION_ALG, 4);
				this.getMIP().setSolveParam(SolveParam.OPTIMALITY_TARGET, 0);
				return super.solveMIP();
			} catch (RuntimeException e2) {
				// Force MIQCP problem type (with quadratic constraints)
				this.getMIP().setSolveParam(SolveParam.LP_OPTIMIZATION_ALG, 0);
				Variable boolVar = new Variable("boolTest", VarType.INT, 0, 1);
				this.getMIP().add(boolVar);
				Constraint quadratic = new Constraint(CompareType.LEQ, 1);
				quadratic.addTerm(new QuadraticTerm(1, boolVar, boolVar));
				this.getMIP().add(quadratic);
				this.getMIP().addObjectiveTerm(1, boolVar);
				return super.solveMIP();
			}
		}
	}

	private final static int NORM_REFERENCE_POINT = -100000;

	private BundleExactValueBids bids;
	private BigDecimal offset;
	private boolean fixNegativeDeltas;
	private Map<Bidder, Set<Bundle>> positiveDeltas;
	private BigDecimal delta;
	private PriceConstraints priceConstraints;

	private Map<Bidder, Map<Bundle, Variable>> deltas = new LinkedHashMap<>();

	@Getter
	private PriceConstraints generatedPriceConstraints;

	public LinearPriceMinimizeDeltasWithNorm(Domain domain, List<UUID> bidders, BundleExactValueBids bids,
			Allocation allocation, PriceConstraints priceConstraints, BigDecimal delta, BigDecimal offset,
			boolean fixNegativeDeltas, Map<Bidder, Set<Bundle>> positiveDeltas, double timeLimit) {
		// set price constraints to empty constraint for super class
		// price constraints will be added in this class directly
		super(domain, bidders, allocation, new PriceConstraints(bidders), timeLimit);
		this.bids = bids;
		this.delta = delta.add(offset.multiply(BigDecimal.valueOf(2)));
		this.offset = offset;
		this.fixNegativeDeltas = fixNegativeDeltas;
		this.positiveDeltas = positiveDeltas;
		this.priceConstraints = priceConstraints;
	}

	@Override
	protected MIPWrapper createMIP() {

		MIPWrapper mipWrapper = MIPWrapper.makeNewMinMIP();

		// Constraints
		Constraint c;
		int varNr = 0;
		for (Bidder bidder : this.getBidders()) {
			BundleExactValueBid values = bids.getBid(bidder);

			Bundle allocated = this.getAllocation().allocationOf(bidder).getBundle();
			BigDecimal allocatedValue = values.getBidForBundle(allocated).getAmount();

			Map<Bundle, Variable> dMap = new LinkedHashMap<>();
			this.deltas.put(bidder, dMap);

			for (BundleExactValuePair bid : values.getBundleBids()) {
				// do not consider allocated bundle as the delta of this bundle is 0 by
				// definition
				if (!bid.getBundle().equals(allocated)) {
					BigDecimal value = allocatedValue.subtract(bid.getAmount());
					c = mipWrapper
							.beginNewLEQConstraint(value.add(offset.divide(BigDecimal.valueOf(100))).doubleValue());
					this.addPriceVariables(c, allocated, bid.getBundle());

					Variable delta = mipWrapper.makeNewDoubleVar("Bid Delta " + (++varNr));

					// Set variable bounds with constraints as they are weaker (otherwise many
					// infeasible QPs)
					BigDecimal maxDeltaValue;
					if (this.positiveDeltas.get(bidder).contains(bid.getBundle())) {
						maxDeltaValue = this.delta;
					} else {
						maxDeltaValue = BigDecimal.ZERO.min(this.delta);
					}

					// set max delta to already constrained delta if this is lower than the value
					// calculated before
					if (this.priceConstraints.getConstrainedBids(bidder.getId()).contains(bid.getBundle())) {
						maxDeltaValue = maxDeltaValue.min(this.priceConstraints
								.getRightHandSide(bidder.getId(), bid.getBundle()).subtract(value)
								.add(offset.divide(BigDecimal.valueOf(10), offset.scale() + 2, RoundingMode.HALF_UP)));
					}

					// add the delta variable if the max delta is not zero
					// or the respective delta should be fixed (i.e. minimized)
					if (maxDeltaValue.compareTo(BigDecimal.ZERO) != 0 || this.fixNegativeDeltas
							|| this.positiveDeltas.get(bidder).contains(bid.getBundle())) {
						// add constraint instead of upper bound on delta, because it is more likely to
						// be feasible
						Constraint c2 = mipWrapper.beginNewLEQConstraint(maxDeltaValue.doubleValue());
						c2.addTerm(1, delta);
						mipWrapper.add(c2);

						c.addTerm(-1, delta);
					}

					if (this.fixNegativeDeltas || this.positiveDeltas.get(bidder).contains(bid.getBundle())) {
						dMap.put(bid.getBundle(), delta);
						mipWrapper.addObjectiveTerm(new QuadraticTerm(0.0001, delta, delta));
						mipWrapper.addObjectiveTerm(new LinearTerm(-2 * NORM_REFERENCE_POINT, delta));
					}
					mipWrapper.add(c);
				}
			}
		}

		mipWrapper.setSolveParam(SolveParam.MARKOWITZ_TOLERANCE, 0.9);
		mipWrapper.setSolveParam(SolveParam.OPTIMALITY_TARGET, 3);
		mipWrapper.setSolveParam(SolveParam.LP_OPTIMIZATION_ALG, 1);

		return mipWrapper;
	}

	@Override
	protected LinearPrices adaptMIPResult(ISolution result) {
		BigDecimal maxValue = BigDecimal.ZERO;
		int numberOfDeltas = 0;
		int numberOfPositiveDeltas = 0;

		Map<UUID, Map<Bundle, BigDecimal>> constraints = new LinkedHashMap<>();

		for (Bidder bidder : this.getBidders()) {

			constraints.put(bidder.getId(), new LinkedHashMap<>());

			BundleExactValueBid values = bids.getBid(bidder);

			Bundle allocated = this.getAllocation().allocationOf(bidder).getBundle();
			BigDecimal allocatedValue = values.getBidForBundle(allocated).getAmount();

			for (BundleExactValuePair bid : values.getBundleBids()) {

				BigDecimal value = allocatedValue.subtract(values.getBidForBundle(bid.getBundle()).getAmount());

				BigDecimal delta = BigDecimal.ZERO;
				if (this.deltas.get(bidder).containsKey(bid.getBundle())) {
					delta = BigDecimal.valueOf(result.getValue(this.deltas.get(bidder).get(bid.getBundle())))
							.setScale(6, RoundingMode.HALF_UP);
					// fancy rounding to avoid infeasibility of next MIP but remain accurate
					if (delta.compareTo(BigDecimal.ZERO) > 0
							|| this.positiveDeltas.get(bidder).contains(bid.getBundle()))
						delta = delta.add(offset);
					else
						delta = BigDecimal.ZERO.min(delta.add(offset));

					// statistics
					numberOfDeltas++;
					if (delta.compareTo(BigDecimal.ZERO) > 0)
						numberOfPositiveDeltas++;
					if (delta.compareTo(maxValue) > 0)
						maxValue = delta;
				}

				BigDecimal rightHandSide = value.add(delta);
				if (this.priceConstraints.getConstrainedBids(bidder.getId()).contains(bid.getBundle())) {
					BigDecimal rh2 = this.priceConstraints.getRightHandSide(bidder.getId(), bid.getBundle())
							.add(this.offset.scaleByPowerOfTen(1));
					rightHandSide = rightHandSide.min(rh2);
				}

				constraints.get(bidder.getId()).put(bid.getBundle(), rightHandSide);
			}
		}

		this.generatedPriceConstraints = new PriceConstraints(constraints);

		log.debug("Max Value: {}", maxValue);
		log.debug("Number of Deltas: {}", numberOfDeltas);
		log.debug("Number of Positive Deltas: {}", numberOfPositiveDeltas);
		return super.adaptMIPResult(result);
	}

	@Override
	protected String getMIPName() {
		return "minimize-norm-delta";
	}

}
