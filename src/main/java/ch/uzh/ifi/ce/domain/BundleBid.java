package ch.uzh.ifi.ce.domain;

import ch.uzh.ifi.ce.mechanisms.ccg.constraintgeneration.PotentialCoalition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(BundleBid.class);


    private BigDecimal amount;
    private final Map<Good, Integer> bundle = new HashMap<>();
    private final String id;

    /**
     * @param amount Bid amount
     * @param bundle Goods to bid on
     * @param id Same id as BundleValue
     */
    public BundleBid(BigDecimal amount, Set<Good> bundle, String id) {
        this.amount = amount;
        bundle.forEach(good -> this.bundle.put(good, 1));
        this.id = id;
    }

    /**
     * @param amount Bid amount
     * @param bundleMap Goods with quantities to bid on
     * @param id Same id as BundleValue
     */
    public BundleBid(BigDecimal amount, Map<Good, Integer> bundleMap, String id) {
        this.amount = amount;
        this.bundle.putAll(bundleMap);
        this.id = id;

    }


    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * @return The {@link Good}s that this Bid bids on
     */
    @Deprecated
    public Set<Good> getBundle() {
        if (bundle.values().stream().anyMatch(n -> n > 1)) {
            // TODO: Fix this
            LOGGER.error("Retrieving simple bundle when there are quantities greater than 1 involved!");
        }
        return Collections.unmodifiableSet(bundle.keySet());
    }

    /**
     * @return The {@link Good}s that this Bid bids on
     */
    public Map<Good, Integer> getBundleWithQuantities() {
        return Collections.unmodifiableMap(bundle);
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

        return new PotentialCoalition(bundle.keySet(), bidder, amount);
    }

}
