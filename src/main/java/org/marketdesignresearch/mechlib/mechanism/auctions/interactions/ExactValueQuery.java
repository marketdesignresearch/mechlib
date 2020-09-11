package org.marketdesignresearch.mechlib.mechanism.auctions.interactions;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBid;

/**
 * A value query where bidder must report exact values.
 * 
 * @author Manuel Beyeler
 * @see ValueQuery
 * @see BundleExactValueBid
 */
public interface ExactValueQuery extends ValueQuery<BundleExactValueBid> {
}
