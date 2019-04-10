package ch.uzh.ifi.ce.mechanisms.ccg.constraintgeneration;

import ch.uzh.ifi.ce.domain.Bidder;
import ch.uzh.ifi.ce.domain.Bundle;
import ch.uzh.ifi.ce.domain.Good;

import java.math.BigDecimal;
import java.util.Set;

/**
 * A potential coalition is not in conflict with itself and can thus form a
 * coalition
 * 
 * @author Benedikt
 *
 */
public class PotentialCoalition {

    private final Bundle bundle;
    private final Bidder bidder;
    private final BigDecimal value;

    public PotentialCoalition(Set<Good> goods, Bidder bidder, BigDecimal value) {
        this(Bundle.singleGoods(goods), bidder, value);
    }

    public PotentialCoalition(Bundle goods, Bidder bidder, BigDecimal value) {
        this.bundle = goods;
        this.bidder = bidder;
        this.value = value;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public Bidder getBidder() {
        return bidder;
    }

    public BigDecimal getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "PotentialCoalition[bidder=" + bidder + " ,value=" + value + "]";
    }

}
