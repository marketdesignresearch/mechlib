package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBids;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.MachineLearningAllocationInferrer;

public class BoundDistributedSVR extends DistributedSVR<BundleBoundValueBids>{

	public BoundDistributedSVR(SupportVectorSetup setup) {
		super(setup);
	}

	@Override
	public MachineLearningAllocationInferrer getMLFunction(BundleBoundValueBids bids) {
		return new BoundSupportVector(this.getSetup(), bids, this.getMipInstrumentation());
	}

}
