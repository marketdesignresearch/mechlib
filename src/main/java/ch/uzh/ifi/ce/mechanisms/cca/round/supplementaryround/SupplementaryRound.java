package ch.uzh.ifi.ce.mechanisms.cca.round.supplementaryround;

import ch.uzh.ifi.ce.domain.Bid;
import ch.uzh.ifi.ce.domain.Bidder;

public interface SupplementaryRound {
    Bid getSupplementaryBids(Bidder bidder);
}
