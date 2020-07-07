package org.marketdesignresearch.mechlib.mechanism.auctions.mlca;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;

/**
 * TODO documentation
 * 
 * @author Manuel
 *
 * @param <T>
 */
public interface MachineLearningComponent<T extends BundleValueBids<?>> {
		MachineLearningAllocationInferrer getMLFunction(T bids);
}
