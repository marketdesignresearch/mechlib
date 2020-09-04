package org.marketdesignresearch.mechlib.core.allocationlimits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.BundleEntry;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.bundlesampling.UniformRandomBundleSampling;
import org.marketdesignresearch.mechlib.utils.CPLEXUtils;

import com.google.common.base.Preconditions;

import edu.harvard.econcs.jopt.solver.IMIP;
import edu.harvard.econcs.jopt.solver.MIPInfeasibleException;
import edu.harvard.econcs.jopt.solver.SolveParam;
import edu.harvard.econcs.jopt.solver.mip.CompareType;
import edu.harvard.econcs.jopt.solver.mip.Constraint;
import edu.harvard.econcs.jopt.solver.mip.MIPWrapper;
import edu.harvard.econcs.jopt.solver.mip.VarType;
import edu.harvard.econcs.jopt.solver.mip.Variable;
import lombok.Getter;

/**
 * A bidder specific allocation limit. Using this allocation limit it is possible to restrict 
 * a bidders final allocation using linear constraints on Goods. These allocation limits are
 * automatically respected by any WDP in mechlib and it is possibly to add them in a very 
 * generic way to any other kind of WDP problem.
 * 
 * Note that an allocation limit is always bidder and domain specific.
 * 
 * @author Manuel Beyeler
 */
public abstract class AllocationLimit {

	/**
	 * The linear constraints of this AllocationLimit.
	 */
	@Getter
	private List<AllocationLimitConstraint> constraints = new ArrayList<>();
	/**
	 * Additional (JOpt) Variables that do not correspond to goods but may be used
	 * to formulate more complicated linear constraint. When you implement a new
	 * WDP that respects AllocationLimits make sure to take these variables into
	 * account and possibly add them to the JOpt problem.
	 */
	@Getter
	private LinkedHashSet<Variable> additionalVariables = new LinkedHashSet<>();
	
	/**
	 * A MIP that is used to verify if a certain bundle is allocatable with respect
	 * to this AllocationLimit
	 * 
	 * @see #validate(Bundle)
	 * @see #getUniformRandomBundle(Random, List)
	 */
	private IMIP validationMIP;
	private Map<Good,Variable> validationGoodVariables = new HashMap<>();
	
	/**
	 * @param goodList all goods in the respective domain
	 */
	public AllocationLimit(List<? extends Good> goodList) {
		this.validationMIP = MIPWrapper.makeNewMaxMIP();
		this.validationMIP.setSolveParam(SolveParam.CALCULATE_CONFLICT_SET, false);
		this.validationMIP.setSolveParam(SolveParam.CONSTRAINT_BACKOFF_LIMIT, 0);
		
		int varCount = 1;
		for(Good good : goodList) {
			Variable var = new Variable("ALGood"+(varCount++), VarType.INT, 0, good.getQuantity());
			this.validationMIP.add(var);
			this.validationMIP.addObjectiveTerm(1, var);
			this.validationGoodVariables.put(good, var);
		}
	}

	/**
	 * Default AllocationLimit which allows a bidder to win any bundle.
	 */
	public static AllocationLimit NO = new AllocationLimit(new ArrayList<>()) {
		@Override
		public boolean validate(Bundle bundle) {
			return true;
		}
		
		public boolean validateDomainCompatiblity(List<? extends Good> domainGoods) {
			return true;
		}

		@Override
		public Bundle getUniformRandomBundle(Random random, List<? extends Good> goods) {
			return new UniformRandomBundleSampling(random).getSingleBundle(goods);
		}

		@Override
		public int calculateAllocationBundleSpace(List<? extends Good> startingSpace) {
			return (int) Math.pow(2, startingSpace.stream().mapToInt(Good::getQuantity).sum());
		}
	};

	/**
	 * Add linear constraints on goods for this AllocationLimit
	 * @param constraint constraint to add
	 * @see AllocationLimitConstraint
	 */
	protected void addAllocationLimitConstraint(AllocationLimitConstraint constraint) {
		this.constraints.add(constraint);
		this.validationMIP.add(constraint.createCPLEXConstraint(this.validationGoodVariables));
		List<Variable> newVars = new ArrayList<>(constraint.getAdditionalVariables());
		newVars.removeAll(this.getAdditionalVariables());
		newVars.forEach(validationMIP::add);
		this.additionalVariables.addAll(newVars);
	}

	/**
	 * Calculate the number of allocatable bundles (i.e. the size of the feasible set of bundles
	 * with respect to this AllocationLimit).
	 * @param startingSpace
	 * @return
	 */
	public abstract int calculateAllocationBundleSpace(List<? extends Good> startingSpace);

	
	/**
	 * Samples a bundle uniformly at random from the set resulting from the intersection of the 
	 * set of feasible bundles with respect to this AllocationLimit and the bundle space induced
	 * by the given list of goods
	 * 
	 * @param random The random object used to sample the bundle 
	 * @param goods The returned bundle is limited to contain only goods from this list
	 * @return the sampled bundle
	 */
	public abstract Bundle getUniformRandomBundle(Random random, List<? extends Good> goods);

	/**
	 * Validate whether a bundle is allocatable with respect this AllocationLimit
	 * @param bundle the bundle to validate
	 * @return true if this bundle is allocatable, otherwise false
	 */
	public boolean validate(Bundle bundle) {
		Preconditions.checkArgument(this.validateDomainCompatiblity(bundle.getBundleEntries().stream().map(BundleEntry::getGood).collect(Collectors.toList())));
		IMIP testMIP = this.validationMIP.typedClone();
		for(Map.Entry<Good, Variable> entry : this.validationGoodVariables.entrySet()) {
			Constraint c = new Constraint(CompareType.EQ,bundle.countGood(entry.getKey()));
			c.addTerm(1, entry.getValue());
			testMIP.add(c);
		}
		
		try {
			CPLEXUtils.SOLVER.solve(testMIP);
			return true;
		} catch (MIPInfeasibleException mie) {
			return false;
		}
	}

	public boolean validateDomainCompatiblity(List<? extends Good> domainGoods) {
		return this.validationGoodVariables.keySet().containsAll(domainGoods);
	}
}
