package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentation;

/**
 * See {@link SupportVector}. This version works with {@link BundleExactValueBids}.
 * 
 * @author Manuel Beyeler
 */
public class ExactSupportVector extends SupportVector<BundleExactValueBid, BundleExactValueBids> {

	public ExactSupportVector(SupportVectorSetup setup, BundleExactValueBids bids,
			MipInstrumentation mipInstrumentation) {
		super(setup, bids, mipInstrumentation);
	}

	@Override
	protected SupportVectorMIP<BundleExactValueBid> createSupportVectorMIP(SupportVectorSetup setup,
			BundleExactValueBid bid) {
		return new ExactSupportVectorMIP(setup, bid, this.getMipInstrumentation());
	}
}
