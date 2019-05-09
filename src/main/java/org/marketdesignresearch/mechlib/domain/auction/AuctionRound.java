package org.marketdesignresearch.mechlib.domain.auction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.marketdesignresearch.mechlib.domain.bid.Bids;
import org.marketdesignresearch.mechlib.mechanisms.AuctionResult;

@RequiredArgsConstructor
public class AuctionRound {

    @Getter
    private final int roundNumber;
    @Getter
    private final Bids bids;
    @Setter @Getter
    private AuctionResult auctionResult;

}
