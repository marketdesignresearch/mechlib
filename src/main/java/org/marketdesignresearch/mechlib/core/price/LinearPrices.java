package org.marketdesignresearch.mechlib.core.price;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.BundleEntry;
import org.marketdesignresearch.mechlib.core.Good;
import org.springframework.data.annotation.PersistenceConstructor;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@AllArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({ @PersistenceConstructor }))
@ToString
@EqualsAndHashCode
public class LinearPrices implements Prices {
	private final Map<UUID, Price> priceMap;
	private final Set<Good> goods;

	public LinearPrices(List<? extends Good> goods) {
		this((LinkedHashMap<? extends Good, ? extends Price>) new LinkedHashMap<>(goods.stream().collect(Collectors.toMap(g -> g, g -> Price.ZERO, (e1, e2) -> e1, LinkedHashMap::new))));
	}

	public LinearPrices(Map<? extends Good, ? extends Price> goodPriceMap) {
		this.goods = ImmutableSet.copyOf(goodPriceMap.keySet());
		Map<UUID, Price> map = new LinkedHashMap<>();
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
		Map<Good, Price> map = new LinkedHashMap<>();
		priceMap.forEach((k, v) -> map.put(getGood(k), v));
		return map;
	}

	private Good getGood(UUID id) {
		return goods.stream().filter(g -> g.getUuid().equals(id)).findAny().orElseThrow(NoSuchElementException::new);
	}

	@Override
	public Prices divide(BigDecimal divisor) {
		LinkedHashMap<Good, Price> map = new LinkedHashMap<>();
		for (Map.Entry<UUID, Price> entry : this.priceMap.entrySet()) {
			map.put(this.goods.stream().filter(g -> g.getUuid().equals(entry.getKey())).findAny().orElseThrow(),
                    new Price(entry.getValue().getAmount().divide(divisor, RoundingMode.HALF_UP)));
		}
		return new LinearPrices(map);
	}
}
