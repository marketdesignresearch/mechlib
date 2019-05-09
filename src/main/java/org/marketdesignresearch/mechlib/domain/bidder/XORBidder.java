package org.marketdesignresearch.mechlib.domain.bidder;

import lombok.*;
import org.marketdesignresearch.mechlib.domain.bidder.value.BundleValue;
import org.marketdesignresearch.mechlib.domain.bidder.value.XORValue;
import org.marketdesignresearch.mechlib.domain.price.Prices;
import org.marketdesignresearch.mechlib.domain.Bundle;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = "value")
public final class XORBidder implements Bidder, Serializable {
    private static final long serialVersionUID = -4896848195956099257L;
    
    @Getter
    private final String id;
    @Getter
    private final XORValue value;

    public XORBidder(String id) {
        this(id, new XORValue());
    }

    @Override
    public BigDecimal getValue(Bundle bundle) {
        return value.getValueFor(bundle);
    }

    @Override
    public List<Bundle> getBestBundles(Prices prices, int maxNumberOfBundles, boolean allowNegative) {
        List<Bundle> result = value.getOptimalBundleValueAt(prices, maxNumberOfBundles).stream()
                .filter(bundleValue -> allowNegative || bundleValue.getAmount().subtract(prices.getPrice(bundleValue.getBundle()).getAmount()).signum() > 0)
                .map(BundleValue::getBundle).collect(Collectors.toList());
        if (result.isEmpty()) result.add(Bundle.EMPTY);
        return result;
    }
}
