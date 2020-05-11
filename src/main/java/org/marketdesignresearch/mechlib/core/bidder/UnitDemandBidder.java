package org.marketdesignresearch.mechlib.core.bidder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.bidder.newstrategy.InteractionStrategy;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentation;
import org.springframework.data.annotation.PersistenceConstructor;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;
import com.google.common.collect.Sets;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@PersistenceConstructor}))
@EqualsAndHashCode
@ToString(onlyExplicitlyIncluded = true)
public class UnitDemandBidder implements Bidder, Serializable {
    private static final long serialVersionUID = -8537482710326907817L;

    @Getter
    @ToString.Include
    private final UUID id;
    @Getter
    @ToString.Include
    private final String name;
    @Getter
    @ToString.Include
    private final BigDecimal value;
    private final List<Good> goodsOfInterest;
    @Getter
    private final String description;
    @Getter
    private final String shortDescription;

    public UnitDemandBidder(String name, BigDecimal value, List<Good> goodsOfInterest) {
        this.id = UUID.randomUUID();
        this.value = value;
        this.name = name;
        this.goodsOfInterest = goodsOfInterest;
        this.description = "This bidder has unit demand: Zero value if nothing is won, or "
                + value.setScale(2, RoundingMode.HALF_UP) + " (rounded) if any good is won.";
        this.shortDescription = "Unit Demand Bidder: " + getName();
    }

    @Override
    public BigDecimal getValue(Bundle bundle) {
        if (bundle.getBundleEntries().size() > 0) {
            return value;
        } else {
            return BigDecimal.ZERO;
        }
    }

    @Override
    public LinkedHashSet<Bundle> getBestBundles(Prices prices, int maxNumberOfBundles, boolean allowNegative) {
        return Sets.powerSet(new HashSet<>(goodsOfInterest)).stream()
                .map(Bundle::of)
                .sorted((a, b) -> getValue(b).subtract(prices.getPrice(b).getAmount()).compareTo(getValue(a).subtract(prices.getPrice(a).getAmount())))
                .filter(bundle -> allowNegative || value.subtract(prices.getPrice(bundle).getAmount()).signum() > -1)
                .limit(maxNumberOfBundles)
                .collect(Collectors.toCollection(LinkedHashSet::new));
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
		if(!this.strategies.containsKey(type)) this.setStrategy(InteractionStrategy.defaultStrategy(type));
		return  this.strategies.getInstance(type);
	}
	// endregion

    // region instrumentation
    @Override
    public void setMipInstrumentation(MipInstrumentation mipInstrumentation) {
        // No MIP is going to be run
    }
    // endregion
}
