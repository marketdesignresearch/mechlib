package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.refinement;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.ElicitationEconomy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EfficiencyInfo {

	/**
	 * true if the refinement should be stopped
	 */
	@Getter
	private final boolean converged;

	/**
	 * Efficiency information about each economy.
	 */
	@Getter
	private final LinkedHashMap<ElicitationEconomy, ElicitationEconomyEfficiency> elicitationEconomyEfficiency;

	/**
	 * Specific Information about one economy
	 */
	public static class ElicitationEconomyEfficiency {
		/**
		 * the alpha value of this economy (see Lubin (2008))
		 */
		public BigDecimal alpha;

		/**
		 * the efficiency guarantee of the lower bound allocation of this economy (see
		 * Lubin(2008) or Beyeler(2021))
		 */
		public BigDecimal efficiency;
	}
}
