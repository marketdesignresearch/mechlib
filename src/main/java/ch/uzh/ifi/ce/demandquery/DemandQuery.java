package ch.uzh.ifi.ce.demandquery;

import ch.uzh.ifi.ce.domain.Bidder;
import ch.uzh.ifi.ce.domain.BundleBid;
import ch.uzh.ifi.ce.mechanisms.cca.Prices;

import java.util.List;

public interface DemandQuery {

    default BundleBid getBundleBid(String id, Bidder bidder, Prices prices) {
        List<BundleBid> results = getBestBundleBids(id, bidder, prices, 1);
        if (results.size() > 1) System.err.println("Requested one solution, got " + results.size() + ".");
        return results.get(0);
    }

    List<BundleBid> getBestBundleBids(String id, Bidder bidder, Prices prices, int numberOfBundles);

}
