package org.marketdesignresearch.mechlib.core.bidder.valuefunction;

import com.google.common.collect.ImmutableSet;
import lombok.*;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.core.bid.Bid;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.BundleBid;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;
import org.marketdesignresearch.mechlib.winnerdetermination.XORWinnerDetermination;
import org.springframework.data.annotation.PersistenceConstructor;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

@EqualsAndHashCode
@ToString
public class XORValueFunction implements ValueFunction {
    private static final long serialVersionUID = -2661282710326907817L;

    @Getter
    private final Set<BundleValue> bundleValues;

    public XORValueFunction() {
        this(new HashSet<>());
    }

    @PersistenceConstructor
    public XORValueFunction(Set<BundleValue> bundleValues) {
        this.bundleValues = ImmutableSet.copyOf(bundleValues);
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

    @Override
    public WinnerDetermination toWDP(Bidder bidder) {
        return new XORWinnerDetermination(new Bids(Collections.singletonMap(bidder, toBid())));
    }
}

