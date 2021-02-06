package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.refinement;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBids;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.ElicitationEconomy;
import org.marketdesignresearch.mechlib.winnerdetermination.XORWinnerDetermination;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class EfficiencyInfoCreator {
	
	public abstract boolean hasConverged(LinkedHashMap<ElicitationEconomy,EfficiencyInfo.ElicitationEconomyEfficiency> info, BundleBoundValueBids bids);
	
	public EfficiencyInfo getEfficiencyInfo(BundleBoundValueBids bids, List<ElicitationEconomy> elicitationEconomies) {
		LinkedHashMap<ElicitationEconomy, EfficiencyInfo.ElicitationEconomyEfficiency> infoMap = new LinkedHashMap<>();
		for(ElicitationEconomy eco : elicitationEconomies) {
			infoMap.put(eco, this.getElicitationEconomyEfficiency(bids.only(new LinkedHashSet<>(eco.getBidders()))));
		}
		
		return new EfficiencyInfo(this.hasConverged(infoMap, bids), infoMap);
	}

	protected EfficiencyInfo.ElicitationEconomyEfficiency getElicitationEconomyEfficiency(BundleBoundValueBids bids) {
		Allocation lowerBound = new XORWinnerDetermination(bids).getAllocation();
		Allocation perturbed = new XORWinnerDetermination(bids.getPerturbedBids(lowerBound)).getAllocation();

		log.info("Lowerbound Reported Value: " + lowerBound.getTotalAllocationValue().setScale(2, RoundingMode.HALF_UP)
				+ "\tTrue value: " + lowerBound.getTrueSocialWelfare().setScale(2, RoundingMode.HALF_UP));
		log.info("Perturbed Reported Value: " + perturbed.getTotalAllocationValue().setScale(2, RoundingMode.HALF_UP)
				+ "\tTrue value: " + perturbed.getTrueSocialWelfare().setScale(2, RoundingMode.HALF_UP));
		
		EfficiencyInfo.ElicitationEconomyEfficiency info = new EfficiencyInfo.ElicitationEconomyEfficiency();

		info.alpha = lowerBound.getTotalAllocationValue()
				.divide(lowerBound.getTotalAllocationValue(), 10, RoundingMode.HALF_UP).max(BigDecimal.valueOf(0.5))
				.min(BigDecimal.ONE);

		info.efficiency = lowerBound.getTotalAllocationValue().divide(perturbed.getTotalAllocationValue(),
				10, RoundingMode.HALF_UP);

		return info;
	}
}
