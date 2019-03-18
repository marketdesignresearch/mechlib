package ch.uzh.ifi.ce.domain;

import com.sun.istack.internal.NotNull;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
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
    /**
     * 
     */
    private static final long serialVersionUID = 1037198522505712712L;
    public static final BundleValue ZERO = new BundleValue(BigDecimal.ZERO, Collections.emptySet(), "ZEROBUNDLE");
    private BigDecimal amount = BigDecimal.ZERO;
    private final Set<Good> bundle;
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
        this.bundle = bundle;
        this.id = id;

    }

    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * @return The {@link Good}s that this Value is defined on
     */
    public Set<Good> getBundle() {
        return Collections.unmodifiableSet(bundle);
    }

    public long nonDummySize() {
        Predicate<Good> isDummy = Good::isDummyGood;
        return bundle.stream().filter(isDummy.negate()).count();
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
        if (object instanceof BundleValue) {
            BundleValue otherBid = (BundleValue) object;
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

    public BundleBid toBid(UnaryOperator<BigDecimal> valueToBidFunction) {
        return new BundleBid(valueToBidFunction.apply(amount), bundle, id);
    }

    @Override
    public int compareTo(@NotNull BundleValue o) {
        return Comparator.comparing(BundleValue::getAmount).compare(this, o);
    }

}
