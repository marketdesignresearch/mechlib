package org.marketdesignresearch.mechlib.core.bidder.valuefunction;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValuePair;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;
import org.marketdesignresearch.mechlib.winnerdetermination.XORWinnerDetermination;
import org.springframework.data.annotation.PersistenceConstructor;

import com.google.common.collect.ImmutableSet;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class XORValueFunction implements BidTransformableValueFunction {
	private static final long serialVersionUID = -2661282710326907817L;

	@Getter
	private final Set<BundleValue> bundleValues;

	public XORValueFunction() {
		this(new LinkedHashSet<>());
	}

	@PersistenceConstructor
	public XORValueFunction(Set<BundleValue> bundleValues) {
		this.bundleValues = ImmutableSet.copyOf(bundleValues);
	}

	@Override
	public BigDecimal getValue(Bundle bundle) {
		return bundleValues.stream().filter(bundleValue -> bundleValue.getBundle().equals(bundle))
				.max(BundleValue::compareTo).orElse(BundleValue.ZERO).getAmount();
	}

	@Override
	public BundleExactValueBid toBid(UnaryOperator<BigDecimal> bundleBidOperator) {
		Set<BundleExactValuePair> bundleBids = getBundleValues().stream().map(bb -> bb.toBid(bundleBidOperator))
				.collect(Collectors.toCollection(LinkedHashSet::new));
		return new BundleExactValueBid(bundleBids);
	}

	public List<BundleValue> getOptimalBundleValueAt(Prices prices, int maxNumberOfBundles) {
		return this.getOptimalBundleValueAt(prices, maxNumberOfBundles, false);
	}

	public List<BundleValue> getOptimalBundleValueAt(Prices prices, int maxNumberOfBundles, boolean allowNegative) {
		return bundleValues.stream()
				.filter(b -> allowNegative
						|| b.getAmount().subtract(prices.getPrice(b.getBundle()).getAmount()).signum() >= 0)
				.sorted((a, b) -> {
					BigDecimal first = a.getAmount().subtract(prices.getPrice(a.getBundle()).getAmount());
					BigDecimal second = b.getAmount().subtract(prices.getPrice(b.getBundle()).getAmount());
					return second.compareTo(first);
				}).limit(maxNumberOfBundles).collect(Collectors.toList());
	}

	@Override
	public WinnerDetermination toWDP(Bidder bidder) {
		return new XORWinnerDetermination(new BundleExactValueBids(Collections.singletonMap(bidder, toBid())));
	}
}