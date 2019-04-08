package ch.uzh.ifi.ce.demandquery;

import ch.uzh.ifi.ce.domain.*;
import ch.uzh.ifi.ce.mechanisms.cca.Price;
import ch.uzh.ifi.ce.mechanisms.cca.Prices;

import java.util.List;
import java.util.Map;

public interface DemandQuery {

    BundleBid getBundleBid(Bidder bidder, Prices prices);
    List<BundleBid> getBestBundleBids(Bidder bidder, Prices prices, int numberOfBundles);

}
