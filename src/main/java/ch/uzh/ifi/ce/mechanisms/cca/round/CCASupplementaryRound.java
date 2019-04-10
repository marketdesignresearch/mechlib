package ch.uzh.ifi.ce.mechanisms.cca.round;

import ch.uzh.ifi.ce.domain.Bidder;
import ch.uzh.ifi.ce.domain.Bids;
import ch.uzh.ifi.ce.mechanisms.cca.Prices;
import ch.uzh.ifi.ce.demandquery.DemandQuery;
import ch.uzh.ifi.ce.mechanisms.cca.round.supplementaryround.SupplementaryRound;
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
