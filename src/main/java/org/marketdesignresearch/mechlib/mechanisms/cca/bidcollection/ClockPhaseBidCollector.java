package org.marketdesignresearch.mechlib.mechanisms.cca.bidcollection;

import lombok.RequiredArgsConstructor;
import org.marketdesignresearch.mechlib.domain.bid.Bid;
import org.marketdesignresearch.mechlib.domain.bid.Bids;
import org.marketdesignresearch.mechlib.domain.bidder.Bidder;
import org.marketdesignresearch.mechlib.domain.price.Prices;
import org.marketdesignresearch.mechlib.domain.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class ClockPhaseBidCollector {

    private final int roundNumber;
    @Getter
    private final Map<Good, Integer> demand = new HashMap<>();
    private final Prices prices;
    private final Set<? extends Bidder> bidders;

    public Bids collectBids() {

        Bids bids = new Bids();

        for (Bidder bidder : bidders) {
            Bundle bundle = bidder.getBestBundle(prices); // We ignore the value here
            int totalQuantities = bundle.getBundleEntries().stream().mapToInt(BundleEntry::getAmount).sum();
            if (totalQuantities > 0) {
                BigDecimal bidAmount = prices.getPrice(bundle).getAmount();
                for (BundleEntry entry : bundle.getBundleEntries()) {
                    demand.put(entry.getGood(), demand.getOrDefault(entry.getGood(), 0) + entry.getAmount());
                }

                Bid bid = new Bid();
                BundleBid bundleBid = new BundleBid(bidAmount, bundle, "Bidder_" + bidder.getId() + "_Round_" + roundNumber + "_" + bundle.toString());
                bid.addBundleBid(bundleBid);

                bids.setBid(bidder, bid);
            }

        }

        return bids;
    }

}
