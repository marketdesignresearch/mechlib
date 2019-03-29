package ch.uzh.ifi.ce.domain.cats;

import ch.uzh.ifi.ce.domain.*;
import ch.uzh.ifi.ce.strategy.Strategy;
import ch.uzh.ifi.ce.strategy.StrategySpace;
import com.google.common.collect.Maps;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

public final class Domain {
    /**
     *
     */
    private final Values values;
    private final Set<Good> goods;
    private CATSAuction catsAuction = null;

    public Domain(Values values, Set<Good> goods, CATSAuction catsAuction) {
        this.values = values;
        this.goods = Collections.unmodifiableSet(goods);
        this.catsAuction = catsAuction;
    }

    public Domain(Values values, Set<Good> goods) {
        this(values, goods, null);
    }

    public Set<Bidder> getBidders() {
        return values.getBidders();
    }

    public Values getValues() {
        return values;
    }

    /**
     * Assuming agents play truthful
     *
     * @return
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

    public Set<Good> getGoods() {
        return goods;
    }

    public CATSAuction getCatsAuction() {

        return catsAuction;
    }

    public BigDecimal valueOf(Bidder bidder, BidderAllocation bidderAllocation) {
        return getValue(bidder).valueOf(bidderAllocation);
    }

    public BigDecimal totalValueOf(Allocation allocation) {
        return allocation.getWinners().stream().map(b -> valueOf(b, allocation.allocationOf(b))).reduce(BigDecimal.ZERO, BigDecimal::add);
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
