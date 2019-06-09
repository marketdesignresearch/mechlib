package org.marketdesignresearch.mechlib.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.marketdesignresearch.mechlib.domain.bid.Bids;
import org.marketdesignresearch.mechlib.domain.bidder.ORBidder;
import org.marketdesignresearch.mechlib.winnerdetermination.ORWinnerDetermination;

import java.util.List;

@RequiredArgsConstructor
public final class SimpleORDomain implements Domain {

    @Getter
    private final List<ORBidder> bidders;
    @Getter
    private final List<SimpleGood> goods;

    private Allocation efficientAllocation;

    @Override
    public Allocation getEfficientAllocation() {
        if (efficientAllocation == null) {
            efficientAllocation = new ORWinnerDetermination(Bids.fromOBidders(bidders)).getAllocation();
        }
        return efficientAllocation;
    }
}
