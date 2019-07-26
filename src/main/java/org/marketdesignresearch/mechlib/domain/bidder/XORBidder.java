package org.marketdesignresearch.mechlib.domain.bidder;

import lombok.*;
import org.marketdesignresearch.mechlib.domain.bidder.value.BundleValue;
import org.marketdesignresearch.mechlib.domain.bidder.value.XORValue;
import org.marketdesignresearch.mechlib.domain.price.Prices;
import org.marketdesignresearch.mechlib.domain.Bundle;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class XORBidder implements Bidder, Serializable {
    private static final long serialVersionUID = -4896848195956099257L;

    @Getter
    @EqualsAndHashCode.Include
    private final UUID id;

    @Getter
    private final String name;
    @Getter
    @ToString.Exclude
    private final XORValue value;
    @ToString.Exclude
    private final String description;

    public XORBidder(String name) {
        this(name, new XORValue());
    }

    public XORBidder(String name, XORValue value) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.value = value;
        StringBuilder sb = new StringBuilder("Bidder with an XOR-based value function with the following 5 most-valued bundles (rounded):");
        for (BundleValue bundleValue : value.getBundleValues()
                .stream()
                .sorted(Comparator.comparingDouble(bv -> bv.getAmount().doubleValue()))
                .limit(5)
                .collect(Collectors.toList())) {
            sb.append("\n\t- ").append(bundleValue.getBundle()).append(": ").append(bundleValue.getAmount().setScale(2, RoundingMode.HALF_UP));
        }
        this.description = sb.toString();
    }

    @Override
    public BigDecimal getValue(Bundle bundle) {
        BigDecimal result = value.getValueFor(bundle);
        return result;
    }

    @Override
    public List<Bundle> getBestBundles(Prices prices, int maxNumberOfBundles, boolean allowNegative, double relPoolTolerance, double absPoolTolerance, double poolTimeLimit) {
        List<Bundle> result = value.getOptimalBundleValueAt(prices, maxNumberOfBundles).stream()
                .filter(bundleValue -> allowNegative || bundleValue.getAmount().subtract(prices.getPrice(bundleValue.getBundle()).getAmount()).signum() > 0)
                .map(BundleValue::getBundle).collect(Collectors.toList());
        if (result.isEmpty()) result.add(Bundle.EMPTY);
        return result;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getShortDescription() {
        return "XOR-Bidder: " + getName();
    }
}
