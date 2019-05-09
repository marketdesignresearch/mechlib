package org.marketdesignresearch.mechlib.domain.price;

import org.marketdesignresearch.mechlib.domain.Bundle;
import org.marketdesignresearch.mechlib.domain.Good;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ToString
@EqualsAndHashCode
public class LinearPrices implements Prices {
    private Map<Good, Price> priceMap;

    public LinearPrices(Set<Good> goods) {
        priceMap = new HashMap<>();
        goods.forEach(g -> priceMap.put(g, Price.ZERO));
    }

    public LinearPrices(Map<Good, Price> priceMap) {
        this.priceMap = priceMap;
    }

    public Price get(Good good) {
        return priceMap.get(good);
    }

    @Override
    public Price getPrice(Bundle bundle) {
        BigDecimal price = BigDecimal.ZERO;
        for (Map.Entry<Good, Integer> entry : bundle.entrySet()) {
            price = price.add(get(entry.getKey()).getAmount().multiply(BigDecimal.valueOf(entry.getValue())));
        }
        return new Price(price);
    }

    public Set<Map.Entry<Good, Price>> entrySet() {
        return priceMap.entrySet();
    }
}
