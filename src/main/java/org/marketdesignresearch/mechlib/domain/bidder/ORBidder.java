package org.marketdesignresearch.mechlib.domain.bidder;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.math.DoubleMath;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.marketdesignresearch.mechlib.domain.Allocation;
import org.marketdesignresearch.mechlib.domain.Bundle;
import org.marketdesignresearch.mechlib.domain.BundleBid;
import org.marketdesignresearch.mechlib.domain.bid.Bid;
import org.marketdesignresearch.mechlib.domain.bid.Bids;
import org.marketdesignresearch.mechlib.domain.bidder.value.BundleValue;
import org.marketdesignresearch.mechlib.domain.bidder.value.ORValue;
import org.marketdesignresearch.mechlib.domain.bidder.value.Value;
import org.marketdesignresearch.mechlib.domain.bidder.value.XORValue;
import org.marketdesignresearch.mechlib.domain.price.Prices;
import org.marketdesignresearch.mechlib.winnerdetermination.ORWinnerDetermination;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class ORBidder implements Bidder, Serializable {
    private static final long serialVersionUID = -4896848195956099257L;

    @Getter
    @EqualsAndHashCode.Include
    private final UUID id;
    @Getter
    private final String name;
    @Getter
    @ToString.Exclude
    private final ORValue value;
    @ToString.Exclude
    private final String description;

    public ORBidder(String name) {
        this(name, new ORValue());
    }

    public ORBidder(String name, ORValue value) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.value = value;
        StringBuilder sb = new StringBuilder("Bidder with an OR-based value function with the following values (rounded):");
        for (BundleValue bundleValue : value.getBundleValues()) {
            sb.append("\n\t- ").append(bundleValue.getBundle()).append(": ").append(bundleValue.getAmount().setScale(2, RoundingMode.HALF_UP));
        }
        this.description = sb.toString();
    }

    @Override
    public BigDecimal getValue(Bundle bundle) {
        return value.getValueFor(bundle);
    }

    @Override
    public List<Bundle> getBestBundles(Prices prices, int maxNumberOfBundles, boolean allowNegative, double relPoolTolerance, double absPoolTolerance, double poolTimeLimit) {
        Bid valueMinusPrice = new Bid();
        value.getBundleValues().forEach(bundleValue -> valueMinusPrice.addBundleBid(new BundleBid(
                bundleValue.getAmount().subtract(prices.getPrice(bundleValue.getBundle()).getAmount()),
                bundleValue.getBundle(),
                bundleValue.getId())));
        WinnerDetermination orWdp = new ORWinnerDetermination(new Bids(ImmutableMap.of(this, valueMinusPrice)));
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

    @Override
    public String getDescription() {
        return description;
    }
}
