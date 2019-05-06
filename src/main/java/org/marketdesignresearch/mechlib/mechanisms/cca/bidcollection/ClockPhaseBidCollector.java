package org.marketdesignresearch.mechlib.mechanisms.cca.bidcollection;

import lombok.RequiredArgsConstructor;
import org.marketdesignresearch.mechlib.domain.*;
import org.marketdesignresearch.mechlib.mechanisms.cca.Prices;
import org.marketdesignresearch.mechlib.demandquery.DemandQuery;
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
    private final DemandQuery demandQuery;

    public Bids collectBids() {

        Bids bids = new Bids();

        for (Bidder bidder : bidders) {
            Bundle bundle = demandQuery.getBundleBid(String.valueOf(roundNumber), bidder, prices).getBundle(); // We ignore the value here
            int totalQuantities = bundle.values().stream().mapToInt(i -> i).sum();
            if (totalQuantities > 0) {
                BigDecimal bidAmount = BigDecimal.ZERO;
                for (Map.Entry<Good, Integer> entry : bundle.entrySet()) {
                    Good good = entry.getKey();
                    demand.put(good, demand.getOrDefault(good, 0) + entry.getValue());
                    BigDecimal quantityTimesPrice = prices.get(entry.getKey()).getAmount().multiply(BigDecimal.valueOf(entry.getValue()));
                    bidAmount = bidAmount.add(quantityTimesPrice);
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
