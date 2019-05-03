package org.marketdesignresearch.mechlib.mechanisms.cca.round;

import org.marketdesignresearch.mechlib.domain.Bidder;
import org.marketdesignresearch.mechlib.domain.Bids;
import org.marketdesignresearch.mechlib.mechanisms.cca.Prices;
import org.marketdesignresearch.mechlib.demandquery.DemandQuery;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.Set;

public abstract class CCARound {
    @Getter
    private final int roundNumber;
    @Getter
    private final Prices prices;
    private Bids bids;
    @Getter(AccessLevel.PROTECTED)
    private final Set<? extends Bidder> bidders;
    @Getter(AccessLevel.PROTECTED)
    private final DemandQuery demandQuery;

    public CCARound(int roundNumber, Prices prices, Set<? extends Bidder> bidders, DemandQuery demandQuery) {
        this.roundNumber = roundNumber;
        this.prices = prices;
        this.bidders = bidders;
        this.demandQuery = demandQuery;
    }

    public abstract Bids collectBids();

    public Bids getBids() {
        if (bids == null) this.bids = collectBids();
        return bids;
    }
}
