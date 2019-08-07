package org.marketdesignresearch.mechlib.core.price;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.BundleEntry;
import org.marketdesignresearch.mechlib.core.Good;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@ToString
@EqualsAndHashCode
public class LinearPrices implements Prices {
    private final ImmutableMap<UUID, Price> priceMap;
    private final ImmutableSet<Good> goods;

    public LinearPrices(List<? extends Good> goods) {
        this(goods.stream().collect(Collectors.toMap(g -> g, g -> Price.ZERO)));
    }

    public LinearPrices(Map<Good, Price> goodPriceMap) {
        this.goods = ImmutableSet.copyOf(goodPriceMap.keySet());
        Map<UUID, Price> map = new HashMap<>();
        goodPriceMap.forEach((g, p) -> map.put(g.getUuid(), p));
        this.priceMap = ImmutableMap.copyOf(map);
    }

    public Price get(Good good) {
        return priceMap.getOrDefault(good.getUuid(), Price.ZERO);
    }

    @Override
    public Price getPrice(Bundle bundle) {
        BigDecimal price = BigDecimal.ZERO;
        for (BundleEntry entry : bundle.getBundleEntries()) {
            price = price.add(get(entry.getGood()).getAmount().multiply(BigDecimal.valueOf(entry.getAmount())));
        }
        return new Price(price);
    }

    public Map<Good, Price> getPriceMap() {
        Map<Good, Price> map = new HashMap<>();
        priceMap.forEach((k, v) -> map.put(getGood(k), v));
        return map;
    }

    private Good getGood(UUID id) {
        return goods.stream().filter(g -> g.getUuid().equals(id)).findAny().orElseThrow(NoSuchElementException::new);
    }
}
