package org.marketdesignresearch.mechlib.mechanism.auctions.interactions;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBid;
import org.marketdesignresearch.mechlib.core.price.Prices;

public interface BoundValueQueryWithMRPARRefinement extends BoundValueQuery {

	public Bundle getProvisionalAllocation();

	public Prices getPrices();

	public BundleBoundValueBid getLatestActiveBid();

	@Override
	default Class<? extends Interaction> getType() {
		return BoundValueQueryWithMRPARRefinement.class;
	}
}
