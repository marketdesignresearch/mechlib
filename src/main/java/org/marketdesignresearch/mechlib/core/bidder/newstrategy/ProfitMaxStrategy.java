package org.marketdesignresearch.mechlib.core.bidder.newstrategy;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.ProfitMaxQuery;

public interface ProfitMaxStrategy extends InteractionStrategy{
	BundleValueBid<BundleValuePair> applyProfitMaxStrategy(ProfitMaxQuery interaction);
}
