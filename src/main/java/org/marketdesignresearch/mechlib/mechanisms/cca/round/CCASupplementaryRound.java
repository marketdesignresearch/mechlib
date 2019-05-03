package org.marketdesignresearch.mechlib.mechanisms.cca.round;

import org.marketdesignresearch.mechlib.domain.Bidder;
import org.marketdesignresearch.mechlib.domain.Bids;
import org.marketdesignresearch.mechlib.mechanisms.cca.Prices;
import org.marketdesignresearch.mechlib.demandquery.DemandQuery;
import org.marketdesignresearch.mechlib.mechanisms.cca.round.supplementaryround.SupplementaryRound;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
public class CCASupplementaryRound extends CCARound {

    private final SupplementaryRound supplementaryRound;

    public CCASupplementaryRound(int number, Prices prices, Set<? extends Bidder> bidders, DemandQuery demandQuery, SupplementaryRound supplementaryRound) {
        super(number, prices, bidders, demandQuery);
        this.supplementaryRound = supplementaryRound;
    }

    @Override
    public Bids collectBids() {
        Bids bids = new Bids();
        for (Bidder bidder : getBidders()) {
            log.debug("Asking bidder {} for additional bids...", bidder);
            bids.setBid(bidder, supplementaryRound.getSupplementaryBids(String.valueOf(getRoundNumber()), bidder));
            log.debug("Done, bids for bidder {} collected", bidder);
        }
        return bids;
    }

    @Override
    public String toString() {
        return supplementaryRound.getDescription();
    }
}
