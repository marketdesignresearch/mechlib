package ch.uzh.ifi.ce.domain.bidder;

import ch.uzh.ifi.ce.domain.Bid;
import ch.uzh.ifi.ce.domain.Bundle;
import ch.uzh.ifi.ce.domain.BundleBid;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@EqualsAndHashCode(of = "bundleValues")
@ToString
public class Value implements Serializable {
    private static final long serialVersionUID = -2661282710326907817L;

    @Getter
    private final Set<BundleValue> bundleValues;
    @Getter
    private final ValueType valueType;

    public Value(ValueType valueType) {
        this(new HashSet<>(), valueType);
    }

    public boolean addBundleValue(BundleValue bundleValue) {
        return bundleValues.add(bundleValue);
    }

    public BigDecimal getXORValueFor(Bundle bundle) {
        return bundleValues.stream()
                .filter(bundleValue -> bundleValue.getBundle().equals(bundle))
                .max(BundleValue::compareTo).orElse(BundleValue.ZERO).getAmount();
    }

    public Bid toBid(UnaryOperator<BigDecimal> bundleBidOperator) {
        Set<BundleBid> bundleBids = getBundleValues().stream().map(bb -> bb.toBid(bundleBidOperator)).collect(Collectors.toCollection(LinkedHashSet::new));
        return new Bid(bundleBids);
    }
}
