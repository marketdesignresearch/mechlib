package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.refinement;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBids;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.ElicitationEconomy;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.refinement.EfficiencyInfo.ElicitationEconomyEfficiency;

import lombok.Getter;
import lombok.Setter;

public class EfficiencyGuaranteeEfficiencyInfoCreator extends EfficiencyInfoCreator {

	private static final BigDecimal DEFAULT_EFFICIENCY_TOLERANCE = BigDecimal.valueOf(0.99);

	@Setter
	@Getter
	private BigDecimal efficiencyTolerance;

	public EfficiencyGuaranteeEfficiencyInfoCreator() {
		this(DEFAULT_EFFICIENCY_TOLERANCE);
	}

	public EfficiencyGuaranteeEfficiencyInfoCreator(BigDecimal efficiencyTolerance) {
		this.efficiencyTolerance = efficiencyTolerance;
	}

	@Override
	public boolean hasConverged(LinkedHashMap<ElicitationEconomy, ElicitationEconomyEfficiency> info,
			BundleBoundValueBids bids) {
		return info.values().stream().map(i -> i.efficiency.compareTo(this.getEfficiencyTolerance()) >= 0).reduce(false,
				Boolean::logicalOr);
	}

}
