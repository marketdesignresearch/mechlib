package org.marketdesignresearch.mechlib.core.bid;

import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.BundleBid;
import lombok.Value;

@Value
public class SingleItemBid {
    Bidder bidder;
    BundleBid bundleBid;
}
