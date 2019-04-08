package ch.uzh.ifi.ce.domain;

import ch.uzh.ifi.ce.mechanisms.ccg.constraintgeneration.PotentialCoalition;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.emptySet;

/**
 * Immutable Wrapper for an allocation
 * 
 * @author Benedikt Buenz
 * 
 */
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
@Slf4j
public final class BidderAllocation {
    public static final BidderAllocation ZERO_ALLOCATION = new BidderAllocation(BigDecimal.ZERO, emptySet(), emptySet());
    @Getter
    private final BigDecimal value;
    @Getter
    private final Map<Good, Integer> goodsMap; // TODO: Change to bundle
    @Getter
    private final Set<BundleBid> acceptedBids; // TODO: Check if this is needed

    public BidderAllocation(BigDecimal value, Set<Good> goodsMap, Set<BundleBid> acceptedBids) {
        this(value, goodsMap.stream().collect(Collectors.toMap(good -> good, good -> 1)), acceptedBids);
    }

    /**
     * @return The {@link Good}s that this Bid bids on
     */
    @Deprecated
    public Set<Good> getBundle() {
        if (goodsMap.values().stream().anyMatch(n -> n > 1)) {
            // TODO: Fix this
            log.error("Retrieving simple bundle when there are quantities greater than 1 involved!");
        }
        return Collections.unmodifiableSet(goodsMap.keySet());
    }

    public PotentialCoalition getPotentialCoalition(Bidder bidder) {
        return new PotentialCoalition(goodsMap, bidder, value);
    }

}
