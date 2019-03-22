package ch.uzh.ifi.ce.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static java.util.Collections.emptySet;

/**
 * Immutable Wrapper for an allocation
 * 
 * @author Benedikt Buenz
 * 
 */
public final class BidderAllocation {
    private static final Logger LOGGER = LoggerFactory.getLogger(BidderAllocation.class);

    public static final BidderAllocation ZERO_ALLOCATION = new BidderAllocation(BigDecimal.ZERO, emptySet(), emptySet());
    private final BigDecimal totalValue;
    private final Map<Good, Integer> goods;
    private final Set<BundleBid> acceptedBids;

    public BidderAllocation(Set<BundleBid> acceptedBids) {
        Map<Good, Integer> goods = new HashMap<>();
        BigDecimal totalValue = BigDecimal.ZERO;
        for (BundleBid acceptedBid : acceptedBids) {
            goods.putAll(acceptedBid.getBundleWithQuantities());
            totalValue = totalValue.add(acceptedBid.getAmount());
        }
        this.totalValue = totalValue;
        this.goods = goods;
        this.acceptedBids = acceptedBids;
    }

    public BidderAllocation(BigDecimal totalValue, Set<Good> goods, Set<BundleBid> acceptedBids) {
        this.totalValue = totalValue;
        this.goods = new HashMap<>();
        goods.forEach(g -> this.goods.put(g, 1));
        this.acceptedBids = acceptedBids;
    }

    public BidderAllocation(BigDecimal totalValue, Map<Good, Integer> goods, Set<BundleBid> acceptedBids) {
        this.totalValue = totalValue;
        this.goods = goods;
        this.acceptedBids = acceptedBids;
    }

    public BigDecimal getValue() {
        return totalValue;
    }

    @Deprecated
    public Set<Good> getGoods() {
        if (goods.values().stream().anyMatch(n -> n > 1)) {
            // TODO: Fix this
            LOGGER.error("Retrieving simple bundle when there are quantities greater than 1 involved!");
        }
        return Collections.unmodifiableSet(goods.keySet());
    }

    public Map<Good, Integer> getGoodsWithQuantities() {
        return goods;
    }

    @Override
    public int hashCode() {
        return totalValue.hashCode() ^ goods.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        BidderAllocation otherBidderAllocation = (BidderAllocation) obj;
        return goods.equals(otherBidderAllocation.goods)&&getValue().compareTo(otherBidderAllocation.getValue())==0;
    }

    @Override
    public String toString() {
        return "BidderAllocation[" + getValue().setScale(4, RoundingMode.HALF_EVEN) + ", goods=" + goods+"]";
    }

    public Set<BundleBid> getAcceptedBids() {
        return acceptedBids;
    }

    public PotentialCoalition getPotentialCoalition(Bidder bidder) {

        return new PotentialCoalition(goods, bidder, totalValue);
    }

}
