package org.marketdesignresearch.mechlib.core.bidder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValuePair;
import org.marketdesignresearch.mechlib.core.bidder.newstrategy.DefaultStrategyHandler;
import org.marketdesignresearch.mechlib.core.bidder.newstrategy.InteractionStrategy;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.BundleValue;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.ORValueFunction;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentation;
import org.marketdesignresearch.mechlib.winnerdetermination.ORWinnerDetermination;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;
import org.springframework.data.annotation.PersistenceConstructor;

import com.google.common.base.Preconditions;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MutableClassToInstanceMap;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
    public LinkedHashSet<Bundle> getBestBundles(Prices prices, int maxNumberOfBundles, boolean allowNegative) {
    	BundleExactValueBid valueMinusPrice = new BundleExactValueBid();
        value.getBundleValues().forEach(bundleValue -> valueMinusPrice.addBundleBid(new BundleExactValuePair(
                bundleValue.getAmount().subtract(prices.getPrice(bundleValue.getBundle()).getAmount()),
                bundleValue.getBundle(),
                bundleValue.getId())));
        WinnerDetermination orWdp = new ORWinnerDetermination(new BundleExactValueBids(ImmutableMap.of(this, valueMinusPrice)));
        orWdp.setMipInstrumentation(getMipInstrumentation());
        orWdp.setPurpose(MipInstrumentation.MipPurpose.DEMAND_QUERY);
        List<Allocation> optimalAllocations = orWdp.getBestAllocations(maxNumberOfBundles);

        LinkedHashSet<Bundle> result = optimalAllocations.stream()
                .peek(alloc -> {
                        BigDecimal utility = getUtility(alloc.allocationOf(this).getBundle(), prices);
                        BigDecimal totalAllocationValue = alloc.getTotalAllocationValue();
                        // FIXME: The following check is sometimes failing when utility is negative
                        Preconditions.checkArgument(utility.equals(totalAllocationValue),
                                "Utility of %s not equal to total allocation value of %s",
                                utility,
                                totalAllocationValue);
                })
                .map(allocation -> allocation.allocationOf(this).getBundle())
                .filter(bundle -> allowNegative || getUtility(bundle, prices).signum() > -1)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (result.isEmpty()) result.add(Bundle.EMPTY);
        return result;
    }
    
 // region strategy
    // TODO handle persistence
    private ClassToInstanceMap<InteractionStrategy> strategies = MutableClassToInstanceMap.create();
    
    @Override
	public void setStrategy(InteractionStrategy strategy) {
    	strategy.setBidder(this);
		strategy.getTypes().forEach(t -> this.strategies.put(t, strategy));
	}
    
    @Override
	public <T extends InteractionStrategy> T getStrategy(Class<T> type) {
		if(!this.strategies.containsKey(type)) this.setStrategy(DefaultStrategyHandler.defaultStrategy(type));
		return  this.strategies.getInstance(type);
	}
	// endregion

    // region instrumentation
    @Getter @Setter
    private MipInstrumentation mipInstrumentation = MipInstrumentation.NO_OP;
    // endregion
}
