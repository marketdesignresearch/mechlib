package org.marketdesignresearch.mechlib.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.bidder.ORBidder;
import org.marketdesignresearch.mechlib.winnerdetermination.ORWinnerDetermination;

import java.util.List;

@RequiredArgsConstructor
public final class SimpleORDomain implements Domain {

    @Getter
    private final List<? extends ORBidder> bidders;
    @Getter
    private final List<? extends SimpleGood> goods;

    private Allocation efficientAllocation;

    @Override
    public Allocation getEfficientAllocation() {
        if (efficientAllocation == null) {
            efficientAllocation = new ORWinnerDetermination(Bids.fromORBidders(bidders)).getAllocation();
        }
        return efficientAllocation;
    }
}
