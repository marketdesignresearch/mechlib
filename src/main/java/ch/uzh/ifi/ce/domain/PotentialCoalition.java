package ch.uzh.ifi.ce.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A potential coalition is not in conflict with itself and can thus form a
 * coalition
 * 
 * @author Benedikt
 *
 */
public class PotentialCoalition {
    private static final Logger LOGGER = LoggerFactory.getLogger(PotentialCoalition.class);

    private final Map<Good, Integer> goods;
    private final Bidder bidder;
    private final BigDecimal value;

    public PotentialCoalition(Set<Good> goods, Bidder bidder, BigDecimal value) {
        this.goods = new HashMap<>();
        goods.forEach(g -> this.goods.put(g, 1));
        this.bidder = bidder;
        this.value = value;
    }

    public PotentialCoalition(Map<Good, Integer> goods, Bidder bidder, BigDecimal value) {
        this.goods = goods;
        this.bidder = bidder;
        this.value = value;
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
