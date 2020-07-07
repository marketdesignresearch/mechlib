package org.marketdesignresearch.mechlib.mechanism.auctions.mlca;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;

public interface MachineLearningAllocationInferrer {
	default Allocation getInferredEfficientAllocation(Domain domain, ElicitationEconomy economy) {
		return this.getInferredEfficientAllocation(domain, economy, new LinkedHashMap<Bidder, Set<Bundle>>());
	}

	public Allocation getInferredEfficientAllocation(Domain domain, ElicitationEconomy economy,
			Map<Bidder, Set<Bundle>> excludedBundles);
}
