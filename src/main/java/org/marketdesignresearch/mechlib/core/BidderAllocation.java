package org.marketdesignresearch.mechlib.core;

import com.google.common.collect.Sets;
import lombok.*;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.outcomerules.ccg.constraintgeneration.PotentialCoalition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.PersistenceConstructor;

import java.math.BigDecimal;
import java.util.Set;

import static java.util.Collections.emptySet;

/**
 * Immutable Wrapper for an allocation
 * 
 * @author Benedikt Buenz
 * 
 */
@RequiredArgsConstructor(onConstructor = @__({@PersistenceConstructor}))
@ToString
@EqualsAndHashCode
@Slf4j
public final class BidderAllocation {
    public static final BidderAllocation ZERO_ALLOCATION = new BidderAllocation(BigDecimal.ZERO, emptySet(), emptySet());
    @Getter
    private final BigDecimal value;
    @Getter
    private final Bundle bundle;
    @Getter @EqualsAndHashCode.Exclude
    private final Set<BundleValuePair> acceptedBids; // TODO: Check if this is needed

    public BidderAllocation(BigDecimal value, Set<? extends Good> bundle, Set<BundleValuePair> acceptedBids) {
        this(value, Bundle.of(bundle), acceptedBids);
    }

    public PotentialCoalition getPotentialCoalition(Bidder bidder) {
        return new PotentialCoalition(bundle, bidder, value);
    }

    public BidderAllocation merge(BidderAllocation other) {
        // Note: This assumes linear value
        return new BidderAllocation(getValue().add(other.getValue()), getBundle().merge(other.getBundle()), Sets.union(getAcceptedBids(), other.getAcceptedBids()));
    }
}
