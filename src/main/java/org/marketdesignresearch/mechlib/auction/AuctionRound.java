package org.marketdesignresearch.mechlib.auction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.marketdesignresearch.mechlib.domain.bid.Bids;
import org.marketdesignresearch.mechlib.domain.price.Prices;
import org.marketdesignresearch.mechlib.mechanisms.MechanismResult;

@RequiredArgsConstructor
public class AuctionRound {

    @Getter
    private final int roundNumber;
    @Getter
    private final Bids bids;
    @Getter
    private final Prices prices;
    @Setter
    @Getter
    private MechanismResult mechanismResult;

}
