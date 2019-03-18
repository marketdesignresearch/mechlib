package ch.uzh.ifi.ce.domain;

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
    private final Set<Good> goods;
    private final Bidder bidder;
    private final BigDecimal value;

    public PotentialCoalition(Set<Good> goods, Bidder bidder, BigDecimal value) {
        this.goods = goods;
        this.bidder = bidder;
        this.value = value;
    }

    public Set<Good> getGoods() {
        return goods;
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
