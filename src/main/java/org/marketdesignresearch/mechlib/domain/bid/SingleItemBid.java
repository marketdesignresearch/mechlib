package org.marketdesignresearch.mechlib.domain.bid;

import org.marketdesignresearch.mechlib.domain.bidder.Bidder;
import org.marketdesignresearch.mechlib.domain.BundleBid;
import lombok.Value;

@Value
public class SingleItemBid {
    Bidder bidder;
    BundleBid bundleBid;
}
