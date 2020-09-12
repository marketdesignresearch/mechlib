package org.marketdesignresearch.mechlib.core;

import static java.util.Collections.emptySet;

import java.math.BigDecimal;
import java.util.Set;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValuePair;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.outcomerules.ccg.constraintgeneration.PotentialCoalition;
import org.springframework.data.annotation.PersistenceConstructor;

import com.google.common.collect.Sets;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Immutable Wrapper for an allocation
 * 
 * @author Benedikt Buenz
 * 
 */
@RequiredArgsConstructor(onConstructor = @__({ @PersistenceConstructor }))
@ToString
@EqualsAndHashCode
@Slf4j
public final class BidderAllocation {
	public static final BidderAllocation ZERO_ALLOCATION = new BidderAllocation(BigDecimal.ZERO, emptySet(),
			emptySet());
	@Getter
	private final BigDecimal value;
	@Getter
	private final Bundle bundle;
	@Getter
	@EqualsAndHashCode.Exclude
	private final Set<? extends BundleExactValuePair> acceptedBids; // TODO: Check if this is needed

	public BidderAllocation(BigDecimal value, Set<? extends Good> bundle, Set<BundleExactValuePair> acceptedBids) {
		this(value, Bundle.of(bundle), acceptedBids);
	}

	public PotentialCoalition getPotentialCoalition(Bidder bidder) {
		return new PotentialCoalition(bundle, bidder, value);
	}

	public BidderAllocation merge(BidderAllocation other) {
		// Note: This assumes linear value
		return new BidderAllocation(getValue().add(other.getValue()), getBundle().merge(other.getBundle()),
				Sets.union(getAcceptedBids(), other.getAcceptedBids()));
	}
}
