package org.marketdesignresearch.mechlib.mechanisms.vcg;

import lombok.AccessLevel;
import lombok.Getter;
import org.marketdesignresearch.mechlib.domain.bidder.Bidder;
import org.marketdesignresearch.mechlib.domain.bid.Bids;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;

public abstract class BidBasedVCGAuction extends VCGAuction { // FIXME: Naming consistently

    @Getter(AccessLevel.PROTECTED)
    private final Bids bids;

    protected BidBasedVCGAuction(Bids bids) {
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
