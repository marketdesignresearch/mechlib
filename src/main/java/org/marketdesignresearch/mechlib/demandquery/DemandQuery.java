package org.marketdesignresearch.mechlib.demandquery;

import org.marketdesignresearch.mechlib.domain.Bidder;
import org.marketdesignresearch.mechlib.domain.BundleBid;
import org.marketdesignresearch.mechlib.mechanisms.cca.Prices;

import java.util.List;

public interface DemandQuery {

    default BundleBid getBundleBid(String id, Bidder bidder, Prices prices) {
        List<BundleBid> results = getBestBundleBids(id, bidder, prices, 1);
        if (results.size() > 1) System.err.println("Requested one solution, got " + results.size() + ".");
        return results.get(0);
    }

    List<BundleBid> getBestBundleBids(String id, Bidder bidder, Prices prices, int numberOfBundles);

}
