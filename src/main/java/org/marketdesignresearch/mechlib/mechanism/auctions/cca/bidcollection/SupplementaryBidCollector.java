package org.marketdesignresearch.mechlib.mechanism.auctions.cca.bidcollection;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.mechanism.auctions.cca.bidcollection.supplementaryround.SupplementaryRound;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class SupplementaryBidCollector {

    private final int roundNumber;
    private final List<? extends Bidder> bidders;
    private final SupplementaryRound supplementaryRound;
    private final Prices prices;

    public Bids collectBids() {
        Bids bids = new Bids();
        for (Bidder bidder : bidders) {
            log.debug("Asking bidder {} for additional bids...", bidder.getName());
            bids.setBid(bidder, supplementaryRound.getSupplementaryBids(String.valueOf(roundNumber), bidder, prices));
            log.debug("Done, bids for bidder {} collected", bidder.getName());
        }
        return bids;
    }

    @Override
    public String toString() {
        return supplementaryRound.getDescription();
    }

}
