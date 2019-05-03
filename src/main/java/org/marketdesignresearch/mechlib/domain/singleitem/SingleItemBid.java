package org.marketdesignresearch.mechlib.domain.singleitem;

import org.marketdesignresearch.mechlib.domain.Bidder;
import org.marketdesignresearch.mechlib.domain.BundleBid;
import lombok.Value;

@Value
public class SingleItemBid {
    Bidder bidder;
    BundleBid bundleBid;
}
