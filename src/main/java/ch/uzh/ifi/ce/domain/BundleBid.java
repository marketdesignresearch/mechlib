package ch.uzh.ifi.ce.domain;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;

/**
 * Class that represents a Bid of one {@link Bidder} on one bundle of
 * {@link Good}s in a Combinatorial Auction The object is immutable
 * </p>compareTo, equals and hashCode are all based on the id.
 * 
 * @author Benedikt Buenz
 * 
 */
public class BundleBid {
    private BigDecimal amount = BigDecimal.ZERO;
    private final Set<Good> bundle;
    private final String id;

    /**
     * The constructor will add this bid to all the goods
     * 
     * @param amount Bid amount
     * @param bundle Goods to bid on
     * @param id Same id as BundleValue
     */
    public BundleBid(BigDecimal amount, Set<Good> bundle, String id) {
        this.amount = amount;
        this.bundle = bundle;
        this.id = id;

    }

    /**
     * The constructor will add this bid to all the goods
     * 
     * @param amount
     * @param bundle
     * @param id
     */
    public BundleBid(BigDecimal amount, Set<Good> bundle, int id) {
        this(amount, bundle, String.valueOf(id));
    }

    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * @return The {@link Good}s that this Bid bids on
     */
    public Set<Good> getBundle() {
        return Collections.unmodifiableSet(bundle);
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "BundleBid[id=" + id + ", amount=" + amount + ", goods=" + bundle + "]";
    }

    /**
     * based on id
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof BundleBid) {
            BundleBid otherBid = (BundleBid) object;
            return id.equals(otherBid.getId());
        } else {
            return false;
        }

    }

    /**
     * based on id
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public BundleBid reducedBy(BigDecimal amount) {

        return new BundleBid(getAmount().subtract(amount).max(BigDecimal.ZERO), bundle, id);
    }

    public PotentialCoalition getPotentialCoalition(Bidder bidder) {

        return new PotentialCoalition(bundle, bidder, amount);
    }

}
