package org.marketdesignresearch.mechlib.domain.bidder.value;

import lombok.*;
import org.marketdesignresearch.mechlib.domain.price.Prices;
import org.marketdesignresearch.mechlib.domain.bid.Bid;
import org.marketdesignresearch.mechlib.domain.Bundle;
import org.marketdesignresearch.mechlib.domain.BundleBid;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class XORValue implements Value {
    private static final long serialVersionUID = -2661282710326907817L;

    @Getter // TODO: Consider using HashMap from Bundle -> Amount
    private final Set<BundleValue> bundleValues;

    public XORValue() {
        this(new HashSet<>());
    }

    public boolean addBundleValue(BundleValue bundleValue) {
        return bundleValues.add(bundleValue);
    }

    @Override
    public BigDecimal getValueFor(Bundle bundle) {
        return bundleValues.stream()
                .filter(bundleValue -> bundleValue.getBundle().equals(bundle))
                .max(BundleValue::compareTo).orElse(BundleValue.ZERO).getAmount();
    }

    @Override
    public Bid toBid(UnaryOperator<BigDecimal> bundleBidOperator) {
        Set<BundleBid> bundleBids = getBundleValues().stream().map(bb -> bb.toBid(bundleBidOperator)).collect(Collectors.toCollection(LinkedHashSet::new));
        return new Bid(bundleBids);
    }

    public List<BundleValue> getOptimalBundleValueAt(Prices prices, int maxNumberOfBundles) {
        return bundleValues.stream()
                .sorted((a, b) -> {
                    BigDecimal first = a.getAmount().subtract(prices.getPrice(a.getBundle()).getAmount());
                    BigDecimal second = b.getAmount().subtract(prices.getPrice(b.getBundle()).getAmount());
                    return second.compareTo(first);
                })
                .limit(maxNumberOfBundles)
                .collect(Collectors.toList());
    }
}

