package ch.uzh.ifi.ce.domain.singleitem;

import ch.uzh.ifi.ce.domain.Bidder;
import ch.uzh.ifi.ce.domain.BundleBid;
import lombok.Value;

@Value
public class SingleItemBid {
    Bidder bidder;
    BundleBid bundleBid;
}
