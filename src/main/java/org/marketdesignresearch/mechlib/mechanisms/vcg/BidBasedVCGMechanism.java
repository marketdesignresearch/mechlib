package org.marketdesignresearch.mechlib.mechanisms.vcg;

import lombok.AccessLevel;
import lombok.Getter;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;

public abstract class BidBasedVCGMechanism extends VCGMechanism { // FIXME: Naming consistently

    @Getter(AccessLevel.PROTECTED)
    private final Bids bids;

    protected BidBasedVCGMechanism(Bids bids) {
        this.bids = bids;
    }

    @Override
    protected WinnerDetermination getWinnerDetermination() {
        return getWinnerDetermination(getBids());
    }

    @Override
    protected WinnerDetermination getWinnerDeterminationWithout(Bidder bidder) {
        return getWinnerDetermination(getBids().without(bidder));
    }

    protected abstract WinnerDetermination getWinnerDetermination(Bids bids);

}
