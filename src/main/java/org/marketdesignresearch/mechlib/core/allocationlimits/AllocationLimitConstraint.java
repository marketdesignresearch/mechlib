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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * A linear constraint used to restrict a bidders allocatable bundle space.
 * 
 * @author Manuel Beyeler
 * @see AllocationLimit
 */
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class AllocationLimitConstraint {

	/**
	 * A linear term of an AllocationLimitConstraint. You can use linear terms formulated on Goods or on 
	 * additional JOpt Variables.
	 */
	@ToString
	@EqualsAndHashCode
	@RequiredArgsConstructor
	public static abstract class AllocationLimitLinearTerm {
		/**
		 * The coefficient of this linear term.
		 */
		@Getter
		private final double coefficient;
		
		/**
		 * Can be used to generate JOpt linear terms for a WDP problem if every good allocated to a bidder is represented in the
		 * WDP by one or more JOpt Variables. Note that the returned term may contain additional JOpt Variables. All additional
		 * JOpt variables can be obtained using {@link AllocationLimit#getAdditionalVariables()}.
		 * 
		 * @param goodVariables The List of JOpt Variables for every Good that represent 
		 * 			if this good is allocated to the respective bidder 
		 * @return a JOpt Linear Term
		 */
		public abstract List<edu.harvard.econcs.jopt.solver.mip.LinearTerm> getLinearTerms(Map<Good, List<Variable>> goodVariables);
	}
	
	/**
	 * A linear term formulated with respect of a single good.
	 */
	@ToString(callSuper = true)
	@EqualsAndHashCode(callSuper = true)
	public static class LinearGoodTerm extends AllocationLimitLinearTerm {
		@Getter
		private final Good good;
		
		/**
		 * Creates a new LinearGoodTerm
		 * @param coefficient the coefficient of this linear term
		 * @param good the Good
		 */
		public LinearGoodTerm(double coefficient, Good good) {
			super(coefficient);
			this.good = good;
		}

		/**
		 * @see AllocationLimitLinearTerm
		 */
		@Override
		public List<LinearTerm> getLinearTerms(
				Map<Good, List<Variable>> goodVariables) {
			List<LinearTerm> result = new ArrayList<>();
			// Do not add constraint for goods where no variable is available
			// (i.e. the good seems to be not allocatable in this MIP and therefore 
			// this good = 0 is always true)
			if(goodVariables.containsKey(good)) {
				for(Variable var : goodVariables.get(good)) {
					result.add(new edu.harvard.econcs.jopt.solver.mip.LinearTerm(this.getCoefficient(), var));
				}
			}
			return result;
		}
		
	}
	
	/**
	 * A linear term for an additional JOpt variable which allows to formulate more complex constraints.
	 * You do not need to register additional variables in any place. They will be registered automatically
	 * and made available through {@link AllocationLimit#getAdditionalVariables()}.
	 * 
	 * However, note that you need to make sure that your variable (names) are unique. Of course you can use
	 * the same varialbe in multiple constraints and terms to formulate your problem.
	 */
	@ToString(callSuper = true)
	@EqualsAndHashCode(callSuper = true)
	public static class LinearVarTerm extends AllocationLimitLinearTerm {
		/**
		 * The JOpt variable
		 */
		@Getter
		private final Variable variable;
		
		/**
		 * Creates a new LinearVarTerm
		 * @param coefficient the coefficient
		 * @param variable
		 */
		public LinearVarTerm(double coefficient, Variable variable) {
			super(coefficient);
			this.variable = variable;
		}

		/**
		 * @see AllocationLimitLinearTerm#getLinearTerms(Map)
		 */
		@Override
		public List<LinearTerm> getLinearTerms(
				Map<Good, List<Variable>> goodVariables) {
			return List.of(this.getLinearTerm());
		}
		
		/**
		 * @return the linear JOpt term
		 */
		public LinearTerm getLinearTerm() {
			return new LinearTerm(this.getCoefficient(),this.getVariable());
		}
	}

	/**
	 * The JOpt compare type of this constraint
	 */
	@Getter
	private final CompareType type;
	/**
	 * The right hand side constant of this constraint
	 */
	@Getter
	private final double constant;
	/**
	 * the left hand side linear terms of this constraint
	 */
	@Getter
	private List<AllocationLimitLinearTerm> linearTerms = new ArrayList<>();
	
	/**
	 * Additional JOpt variables used in this constraint
	 */
	@Getter
	private LinkedHashSet<Variable> additionalVariables = new LinkedHashSet<>();

	/**
	 * add a new linear term with respect to an allocated good
	 * @param coefficient the coefficient
	 * @param good the good
	 * @see LinearGoodTerm
	 */
	public void addTerm(double coefficient, Good good) {
		this.linearTerms.add(new LinearGoodTerm(coefficient, good));
	}
	
	/**
	 * add a new linear term with an addiationl JOpt variable
	 * @param coefficient the coefficient
	 * @param variable the variable
	 * @see LinearVarTerm
	 */
	public void addTerm(double coefficient, Variable variable) {
		this.linearTerms.add(new LinearVarTerm(coefficient, variable));
		this.additionalVariables.add(variable);
	}

	/**
	 * Can be used by a WDP to create JOpt constraints if for every allocated good for this bidder multiply JOpt
	 * variables exist. Note that this constraint can contain additional JOpt variables which must be added to 
	 * the JOpt problem (see {@link AllocationLimit#getAdditionalVariables()} to get all additional variable of
	 * the problem or {@link AllocationLimitConstraint#getAdditionalVariables()} to get all additional variables
	 * for this constraint. 
	 * 
	 * @param goodVariables the good variables for the respective bidder in the WDP
	 * @return a JOpt constraint
	 */
	public Constraint createCPLEXConstraintWithMultiVarsPerGood(Map<Good, List<Variable>> goodVariables) {
		Constraint c = new Constraint(type, this.constant);
		for (AllocationLimitLinearTerm lt : this.linearTerms) {
			for (LinearTerm cplexLt : lt.getLinearTerms(goodVariables)) {
				c.addTerm(cplexLt);
			}
		}
		return c;
	}

	/**
	 * Creates a JOpt constraint if only one variable per bidder and good exists in the WDP.
	 * @param map the good variables for the respective bidder in the WDP
	 * @return aJOpt constraint
	 * @see AllocationLimitConstraint#createCPLEXConstraintWithMultiVarsPerGood(Map)
	 */
	public Constraint createCPLEXConstraint(Map<Good, Variable> map) {
		return this.createCPLEXConstraintWithMultiVarsPerGood(map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> List.of(e.getValue()), (e1, e2) -> e1, LinkedHashMap::new)));
	}
}
