package ch.uzh.ifi.ce.mechanisms.cca;

import ch.uzh.ifi.ce.domain.Good;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ToString
@EqualsAndHashCode
public class Prices {
    private Map<Good, Price> priceMap;

    public Prices(Set<Good> goods) {
        priceMap = new HashMap<>();
        goods.forEach(g -> priceMap.put(g, Price.ZERO));
    }

    public Prices(Map<Good, Price> priceMap) {
        this.priceMap = priceMap;
    }

    public Price get(Good good) {
        return priceMap.get(good);
    }

    public Set<Map.Entry<Good, Price>> entrySet() {
        return priceMap.entrySet();
    }
}
