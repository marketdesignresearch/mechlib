package org.marketdesignresearch.mechlib.auction.cca.bidcollection;

import lombok.RequiredArgsConstructor;
import org.marketdesignresearch.mechlib.domain.Bundle;
import org.marketdesignresearch.mechlib.domain.BundleBid;
import org.marketdesignresearch.mechlib.domain.BundleEntry;
import org.marketdesignresearch.mechlib.domain.bid.Bid;
import org.marketdesignresearch.mechlib.domain.bid.Bids;
import org.marketdesignresearch.mechlib.domain.bidder.Bidder;
import org.marketdesignresearch.mechlib.domain.price.Prices;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
public class ClockPhaseBidCollector {

    private final int roundNumber;
    private final Prices prices;
    private final List<? extends Bidder> bidders;

    public Bids collectBids() {

        Bids bids = new Bids();

        for (Bidder bidder : bidders) {
            Bundle bundle = bidder.getBestBundle(prices); // We ignore the value here
            int totalQuantities = bundle.getBundleEntries().stream().mapToInt(BundleEntry::getAmount).sum();
            if (totalQuantities > 0) {
                BigDecimal bidAmount = prices.getPrice(bundle).getAmount();
                Bid bid = new Bid();
                BundleBid bundleBid = new BundleBid(bidAmount, bundle, "Bidder_" + bidder.getId() + "_Round_" + roundNumber + "_" + bundle.toString());
                bid.addBundleBid(bundleBid);

                bids.setBid(bidder, bid);
            }

        }

        return bids;
    }

}
