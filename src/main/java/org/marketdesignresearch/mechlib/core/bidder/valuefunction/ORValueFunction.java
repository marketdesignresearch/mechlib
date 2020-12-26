package org.marketdesignresearch.mechlib.core.bidder.valuefunction;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.BundleEntry;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValuePair;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.winnerdetermination.ORWinnerDetermination;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;
import org.springframework.data.annotation.PersistenceConstructor;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class ORValueFunction implements BidTransformableValueFunction {
	private static final long serialVersionUID = -2661282710326907817L;

	@Getter
	private final Set<BundleValue> bundleValues;

	public ORValueFunction() {
		this(new LinkedHashSet<>());
	}

	@PersistenceConstructor
	public ORValueFunction(Set<BundleValue> bundleValues) {
		this.bundleValues = ImmutableSet.copyOf(bundleValues);
	}

	@Override
	public BigDecimal getValue(Bundle bundle) {
		// For now, this assumes that the values are defined per item and availability
		// (additive values)
		bundleValues.forEach(bv -> Preconditions.checkArgument(
				bv.getBundle().getBundleEntries().size() == 1
						&& bv.getBundle().getBundleEntries().iterator().next().getAmount() == 1,
				"OR bidders with bundle values are not supported yet..."));
		BigDecimal value = BigDecimal.ZERO;
		for (BundleEntry entry : bundle.getBundleEntries()) {
			BigDecimal v = bundleValues.stream().filter(bv -> {
				BundleEntry b = bv.getBundle().getBundleEntries().iterator().next();
				return entry.getGood().equals(b.getGood()) && b.getAmount() <= entry.getAmount();
			}).max(BundleValue::compareTo).orElse(BundleValue.ZERO).getAmount();
			value = value.add(v);
		}
		return value;
	}

	@Override
	public BundleExactValueBid toBid(UnaryOperator<BigDecimal> bundleBidOperator) {
		Set<BundleExactValuePair> bundleBids = getBundleValues().stream().map(bb -> bb.toBid(bundleBidOperator))
				.collect(Collectors.toCollection(LinkedHashSet::new));
		return new BundleExactValueBid(bundleBids);
	}

	@Override
	public WinnerDetermination toWDP(Bidder bidder) {
		return new ORWinnerDetermination(new BundleExactValueBids(Collections.singletonMap(bidder, toBid())));
	}
}