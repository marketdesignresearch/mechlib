package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;

public class ExactSupportVector extends SupportVector<BundleExactValueBid,BundleExactValueBids>{

	public ExactSupportVector(SupportVectorSetup setup, BundleExactValueBids bids) {
		super(setup, bids);
	}

	@Override
	protected SupportVectorMIP<BundleExactValueBid> createSupportVectorMIP(SupportVectorSetup setup,
			BundleExactValueBid bid) {
		return new ExactSupportVectorMIP(setup, bid);
	}
}
