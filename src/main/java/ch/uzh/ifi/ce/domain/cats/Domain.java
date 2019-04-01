package ch.uzh.ifi.ce.domain.cats;

import ch.uzh.ifi.ce.domain.*;
import ch.uzh.ifi.ce.strategy.Strategy;
import ch.uzh.ifi.ce.strategy.StrategySpace;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
public final class Domain {

    @Getter
    private final Values values;
    @Getter
    private final Set<Good> goods;
    @Getter
    private final CATSAuction catsAuction;

    public Domain(Values values, Set<Good> goods) {
        this(values, goods, null);
    }

    public Set<Bidder> getBidders() {
        return values.getBidders();
    }

    /**
     * Assuming agents play truthful
     *
     */
    public AuctionInstance toAuction() {
        Bids bids = values.toBids();
        return new AuctionInstance(bids);
    }

    public AuctionInstance toAuction(StrategySpace<?, ?> strategySpace) {
        return toAuction(strategySpace, Optional.empty(), null);
    }

    public AuctionInstance toAuction(StrategySpace<?, ?> strategySpace, Bidder bidder, Strategy specificStrategy) {
        return toAuction(strategySpace, Optional.of(bidder), specificStrategy);
    }

    private AuctionInstance toAuction(StrategySpace<?, ?> strategySpace, Optional<Bidder> bidder, Strategy specificStrategy) {
        Bids bids = new Bids(new HashMap<>(Maps.transformValues(getValues().getValueMap(), strategySpace::applyStrategyTo)));
        bidder.ifPresent(b -> bids.setBid(b, specificStrategy.apply(getValue(b))));
        return new AuctionInstance(bids);
    }

    public Value getValue(Bidder bidder) {
        return getValues().getValue(bidder);
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
