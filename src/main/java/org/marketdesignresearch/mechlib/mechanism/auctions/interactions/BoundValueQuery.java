package org.marketdesignresearch.mechlib.mechanism.auctions.interactions;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBid;

/**
 * A value query where a bidder must specify upper and lower bound values for
 * the queried bundles.
 * @author Manuel Beyeler
 * @see ValueQuery
 * @see BundleBoundValueBid
 */
public interface BoundValueQuery extends ValueQuery<BundleBoundValueBid> {

}
