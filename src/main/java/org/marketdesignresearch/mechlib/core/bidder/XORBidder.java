package org.marketdesignresearch.mechlib.core.bidder;

import lombok.*;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.BundleValue;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.XORValueFunction;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.springframework.data.annotation.PersistenceConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor(onConstructor = @__({@PersistenceConstructor}))
@EqualsAndHashCode
@ToString(onlyExplicitlyIncluded = true)
public class XORBidder implements Bidder, Serializable {
    private static final long serialVersionUID = -4896848195956099257L;

    @Getter
    @ToString.Include
    private final UUID id;
    @Getter
    @ToString.Include
    private final String name;
    @Getter
    private final XORValueFunction value;
    @Getter
    private final String description;
    @Getter
    private final String shortDescription;

    public XORBidder(String name) {
        this(name, new XORValueFunction());
    }

    public XORBidder(String name, XORValueFunction value) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.value = value;
        StringBuilder sb = new StringBuilder("Bidder with an XOR-based value function with the following most-valued bundles (rounded):");
        for (BundleValue bundleValue : value.getBundleValues()
                .stream()
                .sorted(Comparator.comparingDouble(bv -> -bv.getAmount().doubleValue()))
                .limit(5)
                .collect(Collectors.toList())) {
            sb.append("\n\t- ").append(bundleValue.getBundle()).append(": ").append(bundleValue.getAmount().setScale(2, RoundingMode.HALF_UP));
        }
        this.description = sb.toString();
        this.shortDescription = "XOR-Bidder: " + getName();
    }

    @Override
    public BigDecimal getValue(Bundle bundle) {
        return value.getValueFor(bundle);
    }

    @Override
    public List<Bundle> getBestBundles(Prices prices, int maxNumberOfBundles, boolean allowNegative, double relPoolTolerance, double absPoolTolerance, double poolTimeLimit) {
        List<Bundle> result = value.getOptimalBundleValueAt(prices, maxNumberOfBundles).stream()
                .filter(bundleValue -> allowNegative || bundleValue.getAmount().subtract(prices.getPrice(bundleValue.getBundle()).getAmount()).signum() > 0)
                .map(BundleValue::getBundle).collect(Collectors.toList());
        if (result.isEmpty()) result.add(Bundle.EMPTY);
        return result;
    }
}
