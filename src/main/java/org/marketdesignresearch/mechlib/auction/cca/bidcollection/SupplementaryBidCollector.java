package org.marketdesignresearch.mechlib.auction.cca.bidcollection;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.marketdesignresearch.mechlib.domain.price.Prices;
import org.marketdesignresearch.mechlib.domain.bidder.Bidder;
import org.marketdesignresearch.mechlib.domain.bid.Bids;
import org.marketdesignresearch.mechlib.domain.Good;
import org.marketdesignresearch.mechlib.auction.cca.bidcollection.supplementaryround.SupplementaryRound;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class SupplementaryBidCollector {

    private final int roundNumber;
    @Getter
    private final Map<Good, Integer> demand = new HashMap<>();
    private final Prices prices;
    private final List<? extends Bidder> bidders;
    private final SupplementaryRound supplementaryRound;

    public Bids collectBids() {
        Bids bids = new Bids();
        for (Bidder bidder : bidders) {
            log.debug("Asking bidder {} for additional bids...", bidder);
            bids.setBid(bidder, supplementaryRound.getSupplementaryBids(String.valueOf(roundNumber), bidder));
            log.debug("Done, bids for bidder {} collected", bidder);
        }
        return bids;
    }

    @Override
    public String toString() {
        return supplementaryRound.getDescription();
    }

}
