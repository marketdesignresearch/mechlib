package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBids;

public class BoundSupportVector extends SupportVector<BundleBoundValueBid,BundleBoundValueBids>{

	public BoundSupportVector(SupportVectorSetup setup, BundleBoundValueBids bids) {
		super(setup, bids);
	}

	@Override
	protected SupportVectorMIP<BundleBoundValueBid> createSupportVectorMIP(SupportVectorSetup setup,
			BundleBoundValueBid bid) {
		return new BoundSupportVectorMIP(setup, bid);
	}

}
