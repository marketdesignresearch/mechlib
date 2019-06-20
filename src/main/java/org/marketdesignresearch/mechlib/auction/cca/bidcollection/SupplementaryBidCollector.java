package org.marketdesignresearch.mechlib.auction.cca.bidcollection;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.marketdesignresearch.mechlib.auction.cca.bidcollection.supplementaryround.SupplementaryRound;
import org.marketdesignresearch.mechlib.domain.bid.Bids;
import org.marketdesignresearch.mechlib.domain.bidder.Bidder;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class SupplementaryBidCollector {

    private final int roundNumber;
    private final List<? extends Bidder> bidders;
    private final SupplementaryRound supplementaryRound;

    public Bids collectBids() {
        Bids bids = new Bids();
        for (Bidder bidder : bidders) {
            log.debug("Asking bidder {} for additional bids...", bidder.getName());
            bids.setBid(bidder, supplementaryRound.getSupplementaryBids(String.valueOf(roundNumber), bidder));
            log.debug("Done, bids for bidder {} collected", bidder.getName());
        }
        return bids;
    }

    @Override
    public String toString() {
        return supplementaryRound.getDescription();
    }

}
