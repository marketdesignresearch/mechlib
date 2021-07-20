package org.marketdesignresearch.mechlib.mechanism.auctions.mlca;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases.MLQueryPhase;

/**
 * A generic MachineLearningComponent for (i)MLCA ({@link MLQueryPhase}).
 * 
 * @author Manuel Beyeler
 */
public interface MachineLearningComponent<T extends BundleValueBids<?>> {

	/**
	 * Learns the value functions with the given bids and returns a allocation
	 * inferrer (i.e. the component that generate WDP problem for different
	 * economies based on the learned value functions).
	 * 
	 * @param bids the bids that are used to learn the value functions
	 * @return a MachineLearningAllocationInferrer
	 */
	MachineLearningAllocationInferrer getMLFunction(T bids);
}
