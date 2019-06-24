package org.marketdesignresearch.mechlib.domain.bidder.value;

import com.google.common.base.Preconditions;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.NotImplementedException;
import org.marketdesignresearch.mechlib.domain.Bundle;
import org.marketdesignresearch.mechlib.domain.BundleBid;
import org.marketdesignresearch.mechlib.domain.BundleEntry;
import org.marketdesignresearch.mechlib.domain.bid.Bid;
import org.marketdesignresearch.mechlib.domain.price.Prices;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class ORValue implements Value {
    private static final long serialVersionUID = -2661282710326907817L;

    @Getter // TODO: Consider using HashMap from Bundle -> Amount
    private final Set<BundleValue> bundleValues;

    public ORValue() {
        this(new HashSet<>());
    }

    public boolean addBundleValue(BundleValue bundleValue) {
        return bundleValues.add(bundleValue);
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

    public List<BundleValue> getOptimalBundleValueAt(Prices prices, int maxNumberOfBundles) {

        throw new NotImplementedException("No demand queries available yet for an OR bidder");
    }
}

