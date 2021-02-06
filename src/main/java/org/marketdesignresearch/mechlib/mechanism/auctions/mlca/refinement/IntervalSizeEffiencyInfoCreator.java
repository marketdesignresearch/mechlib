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

public class IntervalSizeEffiencyInfoCreator extends EfficiencyGuaranteeEfficiencyInfoCreator{

	private final BigDecimal allocationValueGap;
	
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
