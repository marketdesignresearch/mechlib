package org.marketdesignresearch.mechlib.mechanism.auctions.interactions;

import java.util.Set;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBid;
import org.marketdesignresearch.mechlib.core.price.Prices;

public interface RefinementQuery extends TypedInteraction<BundleBoundValueBid> {

	public Set<RefinementType> getRefinementTypes();

	public Bundle getProvisonalAllocation();

	public Prices getPrices();

	public BundleBoundValueBid getLatestActiveBid();

	@Override
	default Class<RefinementQuery> getType() {
		return RefinementQuery.class;
	}
}
