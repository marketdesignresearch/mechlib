package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.refinement;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.ElicitationEconomy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EfficiencyInfo {
	
	@Getter
	private final boolean converged;
	
	@Getter
	private final LinkedHashMap<ElicitationEconomy, ElicitationEconomyEfficiency> elicitationEconomyEfficiency;
	
	public static class ElicitationEconomyEfficiency {
		public BigDecimal alpha;
		public BigDecimal efficiency;
	}
}
