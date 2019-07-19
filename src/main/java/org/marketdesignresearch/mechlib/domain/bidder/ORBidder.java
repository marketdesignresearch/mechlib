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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = "value")
public final class ORBidder implements Bidder, Serializable {
    private static final long serialVersionUID = -4896848195956099257L;

    @Getter
    private final UUID id;
    @Getter
    private final String name;
    @Getter
    private final ORValue value;

    public ORBidder(String name) {
        this(UUID.randomUUID(), name, new ORValue());
    }

    public ORBidder(String name, ORValue value) {
        this(UUID.randomUUID(), name, value);
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
}
