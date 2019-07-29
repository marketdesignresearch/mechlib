package org.marketdesignresearch.mechlib.auction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.marketdesignresearch.mechlib.core.bid.Bid;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.mechanisms.MechanismResult;
import org.marketdesignresearch.mechlib.mechanisms.ccg.MechanismFactory;

@RequiredArgsConstructor
public class AuctionRoundBuilder {
    private final MechanismFactory mechanism;
    @Getter
    private Bids bids = new Bids();
    private MechanismResult mechanismResult;

    public void setBid(Bidder bidder, Bid bid) {
        mechanismResult = null;
        bids.setBid(bidder, bid);
    }

    public MechanismResult getMechanismResult() {
        if (mechanismResult == null) {
            mechanismResult = mechanism.getMechanism(bids).getMechanismResult();
        }
        return mechanismResult;
    }

    public boolean hasMechanismResult() {
        return mechanismResult != null;
    }

    public void setBids(Bids bids) {
        mechanismResult = null;
        this.bids = bids;
    }
}
