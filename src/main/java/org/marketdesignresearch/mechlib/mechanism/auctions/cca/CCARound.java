package org.marketdesignresearch.mechlib.mechanism.auctions.cca;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.marketdesignresearch.mechlib.mechanism.auctions.DefaultAuctionRound;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.springframework.data.annotation.PersistenceConstructor;

@ToString
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class CCARound extends DefaultAuctionRound {

    @Getter
    private final Type type;

    public CCARound(int roundNumber, Bids bids, Prices prices) {
        this(roundNumber, bids, prices, Type.CLOCK);
    }

    @PersistenceConstructor
    public CCARound(int roundNumber, Bids bids, Prices prices, Type type) {
        super(roundNumber, bids, prices);
        this.type = type;
    }

    @Override
    public String getDescription() {
        switch (type) {
            case CLOCK:
                return "Clock Round " + getRoundNumber();
            case SUPPLEMENTARY:
                return "Supplementary Round";
            default:
                return "Auction Round " + getRoundNumber();
        }
    }

    public enum Type {
        CLOCK,
        SUPPLEMENTARY
    }

}

