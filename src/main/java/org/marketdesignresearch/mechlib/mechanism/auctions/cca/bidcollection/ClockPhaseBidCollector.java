package org.marketdesignresearch.mechlib.mechanism.auctions.cca.bidcollection;

import lombok.RequiredArgsConstructor;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.BundleEntry;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.price.Prices;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
public class ClockPhaseBidCollector {

    private final int roundNumber;
    private final Prices prices;
    private final List<? extends Bidder> bidders;

    public BundleValueBids collectBids() {

        BundleValueBids bids = new BundleValueBids();

        for (Bidder bidder : bidders) {
            Bundle bundle = bidder.getBestBundle(prices); // We ignore the value here
            int totalQuantities = bundle.getBundleEntries().stream().mapToInt(BundleEntry::getAmount).sum();
            if (totalQuantities > 0) {
                BigDecimal bidAmount = prices.getPrice(bundle).getAmount();
                BundleValueBid bid = new BundleValueBid();
                BundleValuePair bundleBid = new BundleValuePair(bidAmount, bundle, "Bidder_" + bidder.getId() + "_Round_" + roundNumber + "_" + bundle.toString());
                bid.addBundleBid(bundleBid);

                bids.setBid(bidder, bid);
            }

        }

        return bids;
    }

}
