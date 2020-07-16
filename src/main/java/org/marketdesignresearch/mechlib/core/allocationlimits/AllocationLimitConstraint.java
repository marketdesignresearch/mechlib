package org.marketdesignresearch.mechlib.core.allocationlimits;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Good;

import edu.harvard.econcs.jopt.solver.mip.CompareType;
import edu.harvard.econcs.jopt.solver.mip.Constraint;
import edu.harvard.econcs.jopt.solver.mip.LinearTerm;
import edu.harvard.econcs.jopt.solver.mip.Variable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * TODO add documentation
 * 
 * @author Manuel
 *
 */
@RequiredArgsConstructor
public class AllocationLimitConstraint {

	@RequiredArgsConstructor
	public static abstract class AllocationLimitLinearTerm {
		@Getter
		private final double coefficient;
		public abstract List<edu.harvard.econcs.jopt.solver.mip.LinearTerm> getLinearTerms(Map<Good, List<Variable>> goodVariables);
	}
	
	public static class LinearGoodTerm extends AllocationLimitLinearTerm {
		@Getter
		private final Good good;
		
		public LinearGoodTerm(double coefficient, Good good) {
			super(coefficient);
			this.good = good;
		}

		@Override
		public List<LinearTerm> getLinearTerms(
				Map<Good, List<Variable>> goodVariables) {
			List<LinearTerm> result = new ArrayList<>();
			for(Variable var : goodVariables.get(good)) {
				result.add(new edu.harvard.econcs.jopt.solver.mip.LinearTerm(this.getCoefficient(), var));
			}
			return result;
		}
		
	}
	
	public static class LinearVarTerm extends AllocationLimitLinearTerm {
		@Getter
		private final Variable variable;
		
		public LinearVarTerm(double coefficient, Variable variable) {
			super(coefficient);
			this.variable = variable;
		}

		@Override
		public List<LinearTerm> getLinearTerms(
				Map<Good, List<Variable>> goodVariables) {
			return List.of(this.getLinearTerm());
		}
		
		public LinearTerm getLinearTerm() {
			return new LinearTerm(this.getCoefficient(),this.getVariable());
		}
	}

	@Getter
	private final CompareType type;
	@Getter
	private final double constant;
	@Getter
	private List<AllocationLimitLinearTerm> linearTerms = new ArrayList<>();
	@Getter
	private LinkedHashSet<Variable> additionalVariables = new LinkedHashSet<>();

	public void addTerm(double coefficient, Good good) {
		this.linearTerms.add(new LinearGoodTerm(coefficient, good));
	}
	
	public void addTerm(double coefficient, Variable variable) {
		this.linearTerms.add(new LinearVarTerm(coefficient, variable));
		this.additionalVariables.add(variable);
	}

	public Constraint createCPLEXConstraintWithMultiVarsPerGood(Map<Good, List<Variable>> goodVariables) {
		Constraint c = new Constraint(type, this.constant);
		for (AllocationLimitLinearTerm lt : this.linearTerms) {
			for (LinearTerm cplexLt : lt.getLinearTerms(goodVariables)) {
				c.addTerm(cplexLt);
			}
		}
		return c;
	}

	public Constraint createCPLEXConstraint(Map<Good, Variable> map) {
		return this.createCPLEXConstraintWithMultiVarsPerGood(map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> List.of(e.getValue()), (e1, e2) -> e1, LinkedHashMap::new)));
	}
}
