package org.marketdesignresearch.mechlib.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.marketdesignresearch.mechlib.mechanisms.AuctionMechanism;
import org.marketdesignresearch.mechlib.mechanisms.AuctionResult;
import org.marketdesignresearch.mechlib.mechanisms.Mechanism;

@RequiredArgsConstructor
public class AuctionRound implements AuctionMechanism {

    private final int roundNumber;
    @Getter
    private final Bids bids;
    private final Mechanism mechanism;
    private AuctionResult auctionResult;

    public AuctionResult getAuctionResult() {
        if (auctionResult != null) return auctionResult;
        auctionResult = mechanism.create(bids).getAuctionResult();
        return auctionResult;
    }

}
