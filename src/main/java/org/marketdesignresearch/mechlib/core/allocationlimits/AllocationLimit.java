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
 * TODO add documentation
 * 
 * @author Manuel Beyeler
 */
public abstract class AllocationLimit {

	@Getter
	private List<AllocationLimitConstraint> constraints = new ArrayList<>();
	@Getter
	private LinkedHashSet<Variable> additionalVariables = new LinkedHashSet<>();
	
	private IMIP validationMIP;
	private Map<Good,Variable> validationGoodVariables = new HashMap<>();
	
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

	protected void addAllocationLimitConstraint(AllocationLimitConstraint constraint) {
		this.constraints.add(constraint);
		this.validationMIP.add(constraint.createCPLEXConstraint(this.validationGoodVariables));
		List<Variable> newVars = new ArrayList<>(constraint.getAdditionalVariables());
		newVars.removeAll(this.getAdditionalVariables());
		newVars.forEach(validationMIP::add);
		this.additionalVariables.addAll(newVars);
	}

	public abstract int calculateAllocationBundleSpace(List<? extends Good> startingSpace);

	public abstract Bundle getUniformRandomBundle(Random random, List<? extends Good> goods);

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
