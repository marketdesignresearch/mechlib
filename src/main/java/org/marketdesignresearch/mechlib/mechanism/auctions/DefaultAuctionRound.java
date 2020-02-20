package org.marketdesignresearch.mechlib.mechanism.auctions;

import lombok.*;

import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;
import org.springframework.data.annotation.PersistenceConstructor;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(onConstructor = @__({@PersistenceConstructor}))
public class DefaultAuctionRound<T extends BundleValuePair> implements AuctionRound<T> {

    @Getter
    private final int roundNumber;
    @Getter
    private final BundleValueBids<T> bids;
    @Getter
    private final Prices prices;
    @Getter @Setter
    private Outcome outcome;

}
