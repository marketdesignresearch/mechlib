package ch.uzh.ifi.ce.domain;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class Value {

    private static final long serialVersionUID = -2661282710326907817L;
    private final Set<BundleValue> bundleValues;
    private final ValueType type;

    public Value(Set<BundleValue> bundleValues, ValueType type) {
        this.bundleValues = bundleValues;
        this.type = type;
    }

    public Value(ValueType type) {
        this(new HashSet<>(), type);
    }

    public boolean addBundleValue(BundleValue bundleValue) {
        return bundleValues.add(bundleValue);
    }

    public Set<BundleValue> getBundleValues() {
        return bundleValues;
    }

    public Bid toBid(UnaryOperator<BigDecimal> bundleBidOperator) {
        Set<BundleBid> bundleBids = getBundleValues().stream().map(bb -> bb.toBid(bundleBidOperator)).collect(Collectors.toCollection(LinkedHashSet::new));
        return new Bid(bundleBids);
    }

    public BigDecimal valueOf(BidderAllocation bidderAllocation) {
        BigDecimal totalValue = BigDecimal.ZERO;
        for (BundleBid bundleBid : bidderAllocation.getAcceptedBids()) {
            BigDecimal bundleValue = getBundleValues().stream().filter(bv -> bundleBid.getId().equals(bv.getId())).findAny().map(BundleValue::getAmount).get();
            totalValue = totalValue.add(bundleValue);
        }
        return totalValue;
    }

    public ValueType getValueType() {
        return type;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (obj.getClass() != this.getClass()) return false;
        Value otherValue = (Value) obj;
        return bundleValues.equals(otherValue.getBundleValues());
    }

    @Override
    public int hashCode() {
        return bundleValues.hashCode();
    }

    @Override
    public String toString() {
        return "Bid[bundleBids=" + bundleValues + "]";
    }

}
