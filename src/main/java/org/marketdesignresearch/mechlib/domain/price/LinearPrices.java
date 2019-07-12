package org.marketdesignresearch.mechlib.domain.price;

import org.marketdesignresearch.mechlib.domain.Bundle;
import org.marketdesignresearch.mechlib.domain.BundleEntry;
import org.marketdesignresearch.mechlib.domain.Good;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ToString
@EqualsAndHashCode
public class LinearPrices implements Prices {
    private final Map<Good, Price> priceMap;

    public LinearPrices(List<? extends Good> goods) {
        priceMap = new HashMap<>();
        goods.forEach(g -> priceMap.put(g, Price.ZERO));
    }

    public LinearPrices(Map<Good, Price> priceMap) {
        this.priceMap = priceMap;
    }

    public Price get(Good good) {
        return priceMap.getOrDefault(good, Price.ZERO);
    }

    @Override
    public Price getPrice(Bundle bundle) {
        BigDecimal price = BigDecimal.ZERO;
        for (BundleEntry entry : bundle.getBundleEntries()) {
            price = price.add(get(entry.getGood()).getAmount().multiply(BigDecimal.valueOf(entry.getAmount())));
        }
        return new Price(price);
    }

    public Set<Map.Entry<Good, Price>> entrySet() {
        return priceMap.entrySet();
    }
}
