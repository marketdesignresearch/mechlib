package ch.uzh.ifi.ce.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * Class that represents a Value of one {@link Bidder} on one bundle of
 * {@link Good}s in a Mechanism.</p> The object is immutable compareTo, equals
 * and hashCode are all based on the id.
 * 
 * @author Benedikt Buenz
 * 
 */
public class BundleValue implements Comparable<BundleValue>, Serializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(BundleValue.class);
    
    private static final long serialVersionUID = 1037198522505712712L;
    public static final BundleValue ZERO = new BundleValue(BigDecimal.ZERO, Collections.emptySet(), "ZEROBUNDLE");
    private BigDecimal amount;
    private final Map<Good, Integer> bundle = new HashMap<>();
    private final String id;

    /**
     * The constructor will add this bid to all the goods
     * 
     * @param amount
     * @param bundle
     * @param id
     */
    public BundleValue(BigDecimal amount, Set<Good> bundle, String id) {
        this.amount = amount;
        bundle.forEach(good -> this.bundle.put(good, 1));
        this.id = id;

    }

    /**
     * @param amount Bid amount
     * @param bundleMap Goods with quantities to bid on
     * @param id Same id as BundleValue
     */
    public BundleValue(BigDecimal amount, Map<Good, Integer> bundleMap, String id) {
        this.amount = amount;
        this.bundle.putAll(bundleMap);
        this.id = id;

    }

    public BigDecimal getAmount() {
        return amount;
    }

    public long nonDummySize() {
        Predicate<Good> isDummy = Good::isDummyGood;
        return bundle.keySet().stream().filter(isDummy.negate()).count();
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "BundleBid[id=" + id + ", amount=" + amount + ", goods=" + bundle + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BundleValue that = (BundleValue) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public BundleBid toBid(UnaryOperator<BigDecimal> valueToBidFunction) {
        return new BundleBid(valueToBidFunction.apply(amount), bundle, id);
    }

    @Override
    public int compareTo(BundleValue o) {
        return Comparator.comparing(BundleValue::getAmount).compare(this, o);
    }

}
