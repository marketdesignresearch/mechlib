package org.marketdesignresearch.mechlib.mechanism.auctions.cca;

import lombok.*;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.mechanism.auctions.DefaultAuctionRound;
import org.springframework.data.annotation.PersistenceConstructor;

import java.util.*;
import java.util.stream.Collectors;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CCAClockRound extends DefaultAuctionRound {

    @Getter
    private final Map<UUID, Integer> overDemand;

    public CCAClockRound(int roundNumber, Bids bids, Prices prices, List<? extends Good> goods) {
        this(roundNumber, bids, prices,
                goods.stream().collect(Collectors.toMap(Good::getUuid, good -> bids.getDemand(good) - good.getQuantity())));
    }

    @PersistenceConstructor
    private CCAClockRound(int roundNumber, Bids bids, Prices prices, Map<UUID, Integer> overDemand) {
        super(roundNumber, bids, prices);
        this.overDemand = overDemand;
    }

    @Override
    public String getDescription() {
        return "Clock Round " + getRoundNumber();
    }

    public String getType() {
        return "CLOCK";
    }

}

