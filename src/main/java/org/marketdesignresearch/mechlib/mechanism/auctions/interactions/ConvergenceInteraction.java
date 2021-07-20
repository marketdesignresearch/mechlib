package org.marketdesignresearch.mechlib.mechanism.auctions.interactions;

import java.math.BigDecimal;
import java.util.Set;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBid;

public interface ConvergenceInteraction extends TypedInteraction<BundleBoundValueBid> {
	BigDecimal getEpsilon();

	Set<Bundle> getBundles();

	BundleBoundValueBid getLatestActiveBid();
}
