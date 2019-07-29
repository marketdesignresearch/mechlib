package org.marketdesignresearch.mechlib.auction.cca;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.marketdesignresearch.mechlib.auction.DefaultAuctionRound;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.price.Prices;

@Slf4j
public class CCARound extends DefaultAuctionRound {

    @Getter
    private Type type;

    public CCARound(int roundNumber, Bids bids, Prices prices) {
        this(roundNumber, bids, prices, Type.CLOCK);
    }

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

