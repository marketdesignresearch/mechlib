package org.marketdesignresearch.mechlib.mechanism.auctions.mlca;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;

public interface MachineLearningComponent<T extends BundleValueBids<?>> {
		public MachineLearningAllocationInferrer getMLFunction(T bids);
}
