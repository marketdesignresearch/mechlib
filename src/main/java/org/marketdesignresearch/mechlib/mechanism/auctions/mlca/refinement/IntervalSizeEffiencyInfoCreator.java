package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.refinement;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBids;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.ElicitationEconomy;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.refinement.EfficiencyInfo.ElicitationEconomyEfficiency;
import org.marketdesignresearch.mechlib.winnerdetermination.XORWinnerDetermination;

/**
 * An EfficiencyInfoCreator with two conditions to stop the refinement:
 * 
 * 1. the efficiency of the allocation (with respect to given bids) should be guaranteed to be higher than the specified efficiency tolerance.
 * 2. the relativ difference between lower and upper bound social welfare of the allocation must be within lower than the specific maximal gap.
 * 
 * @author Manuel Beyeler
 */
public class IntervalSizeEffiencyInfoCreator extends EfficiencyGuaranteeEfficiencyInfoCreator{

	private final BigDecimal allocationValueGap;
	
	/**
	 * @param efficiencyTolerance the minimum efficiency guarantee to consider an allocation as efficient.
	 * @param allowedUncertainty the maximal relativ gap between lower and upper bound social welfare allocation of an allocation to meet the second condition.
	 */
	public IntervalSizeEffiencyInfoCreator(BigDecimal efficiencyTolerance, BigDecimal allowedUncertainty) {
		super(efficiencyTolerance);
		this.allocationValueGap = allowedUncertainty;
	}
	
	@Override
	public boolean hasConverged(LinkedHashMap<ElicitationEconomy, ElicitationEconomyEfficiency> info,
			BundleBoundValueBids bids) {
		
		Allocation allocation = new XORWinnerDetermination(bids).getAllocation();
		
		BigDecimal lowerBoundValue = allocation.getTotalAllocationValue();
		BigDecimal upperBoundValue = allocation.getTradesMap().entrySet().stream().map(e -> e.getValue().getBundle().equals(Bundle.EMPTY) ? BigDecimal.ZERO : bids.getBid(e.getKey()).getBidForBundle(e.getValue().getBundle()).getUpperBound()).reduce(BigDecimal.ZERO, BigDecimal::add);
		
		return upperBoundValue.subtract(lowerBoundValue).divide(upperBoundValue,allocationValueGap.scale()+1,RoundingMode.HALF_UP).compareTo(allocationValueGap) <= 0 && super.hasConverged(info, bids);
	}
	

}
