package ch.uzh.ifi.ce.domain;

import ch.uzh.ifi.ce.domain.bidder.SimpleBidder;
import ch.uzh.ifi.ce.domain.cats.CATSAdapter;
import ch.uzh.ifi.ce.domain.cats.CATSAuction;
import ch.uzh.ifi.ce.domain.cats.CATSParser;
import ch.uzh.ifi.ce.strategy.Strategy;
import ch.uzh.ifi.ce.strategy.StrategySpace;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

@RequiredArgsConstructor
public final class Domain {

    @Getter
    private final Set<SimpleBidder> bidders;
    @Getter
    private final Set<Good> goods;

    /**
     * Assuming agents play truthful
     *
     */
    public AuctionInstance toAuction() {
        Bids bids = Bids.fromSimpleBidders(bidders);
        return new AuctionInstance(bids);
    }

    public AuctionInstance toAuction(StrategySpace<?, ?> strategySpace) {
        return toAuction(strategySpace, null, null);
    }

    public AuctionInstance toAuction(StrategySpace<?, ?> strategySpace, SimpleBidder bidder, Strategy specificStrategy) {
        Bids bids = Bids.fromSimpleBidders(bidders, strategySpace::applyStrategyTo);
        if (bidder != null && specificStrategy != null) bids.setBid(bidder, specificStrategy.apply(bidder.getValue()));
        return new AuctionInstance(bids);
    }

    public int size() {
        return getBidders().size();
    }

    public static Domain fromCatsFile(Path catsFile) throws IOException {
        CATSAdapter adapter = new CATSAdapter();
        CATSAuction auction = new CATSParser().readCatsAuctionBean(catsFile);
        return adapter.adaptToDomain(auction);
    }

}
