package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.MachineLearningAllocationInferrer;

public class ExactDistributedSVR extends DistributedSVR<BundleExactValueBids>{

	public ExactDistributedSVR(SupportVectorSetup setup) {
		super(setup);
	}

	@Override
	public MachineLearningAllocationInferrer getMLFunction(BundleExactValueBids bids) {
		return new ExactSupportVector(this.getSetup(), bids);
	}

	

}
