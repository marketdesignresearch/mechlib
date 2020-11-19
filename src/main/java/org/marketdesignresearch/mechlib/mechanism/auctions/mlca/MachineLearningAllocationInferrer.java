package org.marketdesignresearch.mechlib.mechanism.auctions.mlca;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;

/**
 * Predicts the efficient allocation based on a model trained with previous bids.
 * 
 * @author Manuel Beyeler
 * @see MachineLearningComponent
 */
public interface MachineLearningAllocationInferrer {
	/**
	 * @param domain the domain
	 * @param economy the economy (i.e main or marginal) that should be part of the WDP (or the allation)
	 * @return the predicted efficient allocation for this economy.
	 */
	default Allocation getInferredEfficientAllocation(Domain domain, ElicitationEconomy economy) {
		System.out.println(economy);
		return this.getInferredEfficientAllocation(domain, economy, new LinkedHashMap<Bidder, Set<Bundle>>());
	}

	/**
	 * @param domain the domain
	 * @param economy the economy (i.e main or marginal) that should be part of the WDP (or the allation)
	 * @param excludedBundles a map of bundles that may not be allocated to the bidder specified (limits the set of feasible allocations).
	 * @return the predicted efficient allocation for this economy for the limited set of feasible allocation.
	 */
	public Allocation getInferredEfficientAllocation(Domain domain, ElicitationEconomy economy,
			Map<Bidder, Set<Bundle>> excludedBundles);
}
