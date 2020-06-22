package org.marketdesignresearch.mechlib.core.bid.bundle;

import org.marketdesignresearch.mechlib.core.bidder.Bidder;

import lombok.Value;

@Value
public class SingleItemBid {
    Bidder bidder;
    BundleExactValuePair bundleBid;
}
