package ch.uzh.ifi.ce.domain;

import ch.uzh.ifi.ce.mechanisms.ccg.constraintgeneration.PotentialCoalition;
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

    public Map<Good, Integer> getGoodsWithQuantities() {
        return Collections.unmodifiableMap(goods);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BidderAllocation that = (BidderAllocation) o;
        if (!totalValue.equals(that.totalValue)) return false;
        return goods.equals(that.goods);
    }

    @Override
    public int hashCode() {
        int result = totalValue.hashCode();
        result = 31 * result + goods.hashCode();
        return result;
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
