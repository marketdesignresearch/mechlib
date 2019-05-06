package org.marketdesignresearch.mechlib.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.marketdesignresearch.mechlib.domain.bidder.SimpleBidder;
import org.marketdesignresearch.mechlib.domain.cats.CATSAdapter;
import org.marketdesignresearch.mechlib.domain.cats.CATSAuction;
import org.marketdesignresearch.mechlib.domain.cats.CATSParser;
import org.marketdesignresearch.mechlib.strategy.Strategy;
import org.marketdesignresearch.mechlib.strategy.StrategySpace;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

@RequiredArgsConstructor
public final class Domain {

    @Getter
    private final Set<Bidder> bidders;
    @Getter
    private final Set<Good> goods;

    /**
     * Assuming agents play truthful
     *
     */
    public Bids toAuction() {
        return Bids.fromSimpleBidders(bidders);
    }

    public Bids toAuction(StrategySpace<?, ?> strategySpace) {
        return toAuction(strategySpace, null, null);
    }

    public Bids toAuction(StrategySpace<?, ?> strategySpace, SimpleBidder bidder, Strategy specificStrategy) {
        Bids bids = Bids.fromSimpleBidders(bidders, strategySpace::applyStrategyTo);
        if (bidder != null && specificStrategy != null) bids.setBid(bidder, specificStrategy.apply(bidder.getValue()));
        return bids;
    }

    public static Domain fromCatsFile(Path catsFile) throws IOException {
        CATSAdapter adapter = new CATSAdapter();
        CATSAuction auction = new CATSParser().readCatsAuctionBean(catsFile);
        return adapter.adaptToDomain(auction);
    }

}
