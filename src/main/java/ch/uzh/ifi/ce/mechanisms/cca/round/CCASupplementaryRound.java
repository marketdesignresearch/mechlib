package ch.uzh.ifi.ce.mechanisms.cca.round;

import ch.uzh.ifi.ce.domain.Bidder;
import ch.uzh.ifi.ce.domain.Bids;
import ch.uzh.ifi.ce.mechanisms.cca.Prices;
import ch.uzh.ifi.ce.demandquery.DemandQuery;
import ch.uzh.ifi.ce.mechanisms.cca.round.supplementaryround.SupplementaryRound;

import java.util.Set;

public class CCASupplementaryRound extends CCARound {

    private SupplementaryRound supplementaryRound;

    public CCASupplementaryRound(int number, Prices prices, Set<Bidder> bidders, DemandQuery demandQuery, SupplementaryRound supplementaryRound) {
        super(number, prices, bidders, demandQuery);
        this.supplementaryRound = supplementaryRound;
    }

    @Override
    public Bids collectBids() {
        Bids bids = new Bids();
        for (Bidder bidder : getBidders()) {
            bids.setBid(bidder, supplementaryRound.getSupplementaryBids(bidder));
        }
        return bids;
    }
}
