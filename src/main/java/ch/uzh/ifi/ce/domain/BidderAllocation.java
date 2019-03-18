package ch.uzh.ifi.ce.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.emptySet;

/**
 * Immutable Wrapper for an allocation
 * 
 * @author Benedikt Buenz
 * 
 */
public final class BidderAllocation {
    public static final BidderAllocation ZERO_ALLOCATION = new BidderAllocation(BigDecimal.ZERO, emptySet(), emptySet());
    private final BigDecimal totalValue;
    private final Set<Good> goods;
    private final Set<BundleBid> acceptedBids;

    public BidderAllocation(Set<BundleBid> acceptedBids) {
        Set<Good> goods = new HashSet<>();
        BigDecimal totalValue = BigDecimal.ZERO;
        for (BundleBid acceptedBid : acceptedBids) {
            goods.addAll(acceptedBid.getBundle());
            totalValue = totalValue.add(acceptedBid.getAmount());
        }
        this.totalValue = totalValue;
        this.goods = goods;
        this.acceptedBids = acceptedBids;
    }

    public BidderAllocation(BigDecimal totalValue, Set<Good> goods, Set<BundleBid> acceptedBids) {
        this.totalValue = totalValue;
        this.goods = goods;
        this.acceptedBids = acceptedBids;
    }

    public BigDecimal getValue() {
        return totalValue;
    }

    public Set<Good> getGoods() {
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
