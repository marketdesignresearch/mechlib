package org.marketdesignresearch.mechlib.core.bidder;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import lombok.*;
import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.BundleValue;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.ORValueFunction;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentation;
import org.marketdesignresearch.mechlib.winnerdetermination.ORWinnerDetermination;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;
import org.springframework.data.annotation.PersistenceConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor(onConstructor = @__({@PersistenceConstructor}))
@EqualsAndHashCode
@ToString(onlyExplicitlyIncluded = true)
public class ORBidder implements Bidder, Serializable {
    private static final long serialVersionUID = -4896848195956099257L;

    @Getter
    @ToString.Include
    private final UUID id;
    @Getter
    @ToString.Include
    private final String name;
    @Getter
    private final ORValueFunction value;
    @Getter @Setter(AccessLevel.PROTECTED)
    private String description;
    @Getter @Setter(AccessLevel.PROTECTED)
    private String shortDescription;

    public ORBidder(String name) {
        this(name, new ORValueFunction());
    }

    public ORBidder(String name, ORValueFunction value) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.value = value;
        StringBuilder sb = new StringBuilder("Bidder with an OR-based value function with the following values (rounded):");
        for (BundleValue bundleValue : value.getBundleValues()) {
            sb.append("\n\t- ").append(bundleValue.getBundle()).append(": ").append(bundleValue.getAmount().setScale(2, RoundingMode.HALF_UP));
        }
        this.description = sb.toString();
        this.shortDescription = "OR-Bidder: " + getName();
    }

    @Override
    public BigDecimal getValue(Bundle bundle) {
        return value.getValueFor(bundle);
    }

    @Override
    public List<Bundle> getBestBundles(Prices prices, int maxNumberOfBundles, boolean allowNegative, double relPoolTolerance, double absPoolTolerance, double poolTimeLimit) {
        BundleValueBid<BundleValuePair> valueMinusPrice = new BundleValueBid<>();
        value.getBundleValues().forEach(bundleValue -> valueMinusPrice.addBundleBid(new BundleValuePair(
                bundleValue.getAmount().subtract(prices.getPrice(bundleValue.getBundle()).getAmount()),
                bundleValue.getBundle(),
                bundleValue.getId())));
        WinnerDetermination orWdp = new ORWinnerDetermination(new BundleValueBids<BundleValuePair>(ImmutableMap.of(this, valueMinusPrice)));
        orWdp.setMipInstrumentation(getMipInstrumentation());
        orWdp.setPurpose(MipInstrumentation.MipPurpose.DEMAND_QUERY);
        orWdp.setRelativePoolMode4Tolerance(relPoolTolerance);
        orWdp.setAbsolutePoolMode4Tolerance(absPoolTolerance);
        orWdp.setTimeLimitPoolMode4(poolTimeLimit);
        List<Allocation> optimalAllocations = orWdp.getBestAllocations(maxNumberOfBundles);

        List<Bundle> result = optimalAllocations.stream()
                .peek(alloc -> Preconditions.checkArgument(
                        getUtility(alloc.allocationOf(this).getBundle(), prices).equals(alloc.getTotalAllocationValue())
                ))
                .map(allocation -> allocation.allocationOf(this).getBundle())
                .filter(bundle -> allowNegative || getUtility(bundle, prices).signum() > -1)
                .collect(Collectors.toList());
        if (result.isEmpty()) result.add(Bundle.EMPTY);
        return result;
    }

    // region instrumentation
    @Getter @Setter
    private MipInstrumentation mipInstrumentation = MipInstrumentation.NO_OP;
    // endregion
}
