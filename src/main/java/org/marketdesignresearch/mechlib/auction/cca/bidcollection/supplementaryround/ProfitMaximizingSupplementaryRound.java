package org.marketdesignresearch.mechlib.auction.cca.bidcollection.supplementaryround;

import org.marketdesignresearch.mechlib.domain.bid.Bid;
import org.marketdesignresearch.mechlib.domain.bidder.Bidder;
import org.marketdesignresearch.mechlib.domain.Bundle;
import org.marketdesignresearch.mechlib.domain.BundleBid;
import org.marketdesignresearch.mechlib.auction.cca.CCAuction;
import com.google.common.collect.Sets;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class ProfitMaximizingSupplementaryRound implements SupplementaryRound {

    private static final int DEFAULT_NUMBER_OF_SUPPLEMENTARY_BIDS = 10;

    @Setter
    private int numberOfSupplementaryBids = DEFAULT_NUMBER_OF_SUPPLEMENTARY_BIDS;
    private CCAuction cca;

    public ProfitMaximizingSupplementaryRound(CCAuction cca) {
        this.cca = cca;
    }

    @Override
    public Bid getSupplementaryBids(String id, Bidder bidder) {
        List<Bundle> bestBundles = bidder.getBestBundles(cca.getLatestPrices(), numberOfSupplementaryBids, true);
        List<BundleBid> bestBundleBids = new ArrayList<>();
        // Add with true value for now
        int count = 0;
        for (Bundle bundle : bestBundles) {
            bestBundleBids.add(new BundleBid(bidder.getValue(bundle), bundle, "DQ_" + id + "-" + ++count + "_Bidder_" + bidder));
        }
        return new Bid(Sets.newHashSet(bestBundleBids));
    }

    public ProfitMaximizingSupplementaryRound withNumberOfSupplementaryBids(int numberOfSupplementaryBids) {
        setNumberOfSupplementaryBids(numberOfSupplementaryBids);
        return this;
    }

    @Override
    public String getDescription() {
        return "Profit Maximizing Supplementary round with " + numberOfSupplementaryBids + " bids per bidder";
    }

}
