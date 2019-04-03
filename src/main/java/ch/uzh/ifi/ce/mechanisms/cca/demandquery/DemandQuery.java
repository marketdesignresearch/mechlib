package ch.uzh.ifi.ce.mechanisms.cca.demandquery;

import ch.uzh.ifi.ce.domain.*;
import ch.uzh.ifi.ce.mechanisms.cca.Price;

import java.util.List;
import java.util.Map;

public interface DemandQuery {

    BundleBid getBundleBid(Bidder bidder, Map<Good, Price> goodPriceMap);
    List<BundleBid> getBestBundleBids(Bidder bidder, Map<Good, Price> goodPriceMap, int numberOfBundles);

}
