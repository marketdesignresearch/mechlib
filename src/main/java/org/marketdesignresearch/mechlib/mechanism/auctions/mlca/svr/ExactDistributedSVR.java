package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr.kernels.Kernel;

public class ExactDistributedSVR extends DistributedSVR<BundleExactValueBids> {

	public ExactDistributedSVR(SupportVectorSetup setup) {
		super(setup);
	}

	public ExactDistributedSVR(Kernel kernel) {
		super(kernel);
	}

	@Override
	public ExactSupportVector getMLFunction(BundleExactValueBids bids) {
		return new ExactSupportVector(this.getSetup(), bids, this.getMipInstrumentation());
	}
}
