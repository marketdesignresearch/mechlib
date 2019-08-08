package org.marketdesignresearch.mechlib.mechanism.auctions;

import lombok.*;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.springframework.data.annotation.PersistenceConstructor;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(onConstructor = @__({@PersistenceConstructor}))
public class DefaultAuctionRound implements AuctionRound {

    @Getter
    private final int roundNumber;
    @Getter
    private final Bids bids;
    @Getter
    private final Prices prices;
    @Getter @Setter
    private Outcome outcome;

}
