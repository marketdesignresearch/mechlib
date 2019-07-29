package org.marketdesignresearch.mechlib.core.bidder;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.price.Prices;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class UnitDemandBidder implements Bidder, Serializable {
    private static final long serialVersionUID = -8537482710326907817L;

    @Getter
    @EqualsAndHashCode.Include
    @ToString.Include
    private final UUID id;
    @Getter
    @ToString.Include
    private final String name;
    @Getter
    @ToString.Include
    private final BigDecimal value;
    private final List<Good> goodsOfInterest;
    @Getter
    private final String description;
    @Getter
    private final String shortDescription;

    public UnitDemandBidder(String name, BigDecimal value, List<Good> goodsOfInterest) {
        this.id = UUID.randomUUID();
        this.value = value;
        this.name = name;
        this.goodsOfInterest = goodsOfInterest;
        this.description = "This bidder has unit demand: Zero value if nothing is won, or "
                + value.setScale(2, RoundingMode.HALF_UP) + " (rounded) if any good is won.";
        this.shortDescription = "Unit Demand Bidder: " + getName();
    }

    @Override
    public BigDecimal getValue(Bundle bundle) {
        if (bundle.getBundleEntries().size() > 0) {
            return value;
        } else {
            return BigDecimal.ZERO;
        }
    }

    @Override
    public List<Bundle> getBestBundles(Prices prices, int maxNumberOfBundles, boolean allowNegative, double relPoolTolerance, double absPoolTolerance, double poolTimeLimit) {
        return goodsOfInterest.stream()
                .map(Bundle::of)
                .sorted(Comparator.comparing(a -> value.subtract(prices.getPrice(a).getAmount())))
                .filter(bundle -> allowNegative || value.subtract(prices.getPrice(bundle).getAmount()).signum() > -1)
                .limit(maxNumberOfBundles)
                .collect(Collectors.toList());
    }
}
