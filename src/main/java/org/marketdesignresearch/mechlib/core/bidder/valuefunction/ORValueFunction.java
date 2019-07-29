package org.marketdesignresearch.mechlib.core.bidder.valuefunction;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.BundleBid;
import org.marketdesignresearch.mechlib.core.BundleEntry;
import org.marketdesignresearch.mechlib.core.bid.Bid;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

@EqualsAndHashCode
@ToString
public class ORValueFunction implements ValueFunction {
    private static final long serialVersionUID = -2661282710326907817L;

    @Getter
    private final ImmutableSet<BundleValue> bundleValues;

    public ORValueFunction() {
        this(new HashSet<>());
    }

    public ORValueFunction(Set<BundleValue> bundleValues) {
        this.bundleValues = ImmutableSet.copyOf(bundleValues);
    }

    @Override
    public BigDecimal getValueFor(Bundle bundle) {
        // For now, this assumes that the values are defined per item and availability (additive values)
        bundleValues.forEach(bv -> Preconditions.checkArgument(bv.getBundle().getBundleEntries().size() == 1
                && bv.getBundle().getBundleEntries().iterator().next().getAmount() == 1, "OR bidders with bundle values are not supported yet..."));
        BigDecimal value = BigDecimal.ZERO;
        for (BundleEntry entry : bundle.getBundleEntries()) {
            BigDecimal v = bundleValues.stream()
                    .filter(bv -> {
                        BundleEntry b = bv.getBundle().getBundleEntries().iterator().next();
                        return entry.getGood().equals(b.getGood()) && b.getAmount() <= entry.getAmount();
                    })
                    .max(BundleValue::compareTo).orElse(BundleValue.ZERO).getAmount();
            value = value.add(v);
        }
        return value;
    }

    @Override
    public Bid toBid(UnaryOperator<BigDecimal> bundleBidOperator) {
        Set<BundleBid> bundleBids = getBundleValues().stream().map(bb -> bb.toBid(bundleBidOperator)).collect(Collectors.toCollection(LinkedHashSet::new));
        return new Bid(bundleBids);
    }
}

