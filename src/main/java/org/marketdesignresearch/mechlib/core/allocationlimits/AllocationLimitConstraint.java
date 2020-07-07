package org.marketdesignresearch.mechlib.core.allocationlimits;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Good;

import com.google.common.math.DoubleMath;

import edu.harvard.econcs.jopt.solver.mip.CompareType;
import edu.harvard.econcs.jopt.solver.mip.Constraint;
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
	public static enum Type {
		LEQ ((l,r) -> DoubleMath.fuzzyCompare(l,r,1e-10) <= 0, CompareType.LEQ),
		EQ ((l,r) -> DoubleMath.fuzzyEquals(l,r,1e-10), CompareType.EQ),
		GEQ ((l,r) -> DoubleMath.fuzzyCompare(l,r,1e-10) >= 0, CompareType.GEQ);
		
		private final BiFunction<Double, Double, Boolean> comparator;
		@Getter
		private final CompareType cplexType;
		
		public boolean compare(double leftSide, double rightSide) {
			return comparator.apply(leftSide, rightSide);
		}
	}
	
	@RequiredArgsConstructor
	public static class LinearTerm {
		@Getter
		private final double coefficient;
		@Getter
		private final Good good;
	}
	
	@Getter
	private final Type type;
	@Getter
	private final double constant;
	@Getter
	private List<LinearTerm> linearTerms = new ArrayList<>();
	
	public void addTerm(double coefficient, Good good) {
		this.addTerm(new LinearTerm(coefficient,good));
	}
	
	public void addTerm(LinearTerm term) {
		this.linearTerms.add(term);
	}
	
	public boolean validateConstraint(Bundle bundle) {
		double sum = linearTerms.stream().mapToDouble(l -> l.coefficient * bundle.countGood(l.getGood())).sum();
		return type.compare(sum, this.constant);
	}
	
	public Constraint createCPLEXConstraintList(Map<Good, List<Variable>> goodVariables) {
		Constraint c = new Constraint(type.getCplexType(), this.constant);
		for(LinearTerm lt : this.linearTerms) {
			// TODO a MIP might not contain variables for all goods (e.g. sats MIPS might not contain
			// variables for goods with zero base value
			for(Variable v : goodVariables.getOrDefault((lt.getGood()), new ArrayList<>())) {
				c.addTerm(lt.coefficient, v);
			}
		}
		return c;
	}

	public Constraint createCPLEXConstraint(Map<Good, Variable> map) {
		Constraint c = new Constraint(type.getCplexType(), this.constant);
		for(LinearTerm lt : this.linearTerms) {
			c.addTerm(lt.coefficient, map.get(lt.getGood()));
		}
		return c;
	}
	
}
