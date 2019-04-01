package ch.uzh.ifi.ce.domain;

import ch.uzh.ifi.ce.mechanisms.ccg.constraintgeneration.PotentialCoalition;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
public final class BidderAllocation {
    public static final BidderAllocation ZERO_ALLOCATION = new BidderAllocation(BigDecimal.ZERO, emptySet(), emptySet());
    @Getter
    private final BigDecimal value;
    @Getter
    private final Map<Good, Integer> goods;
    @Getter
    private final Set<BundleBid> acceptedBids; // TODO: Check if this is needed

    public BidderAllocation(BigDecimal value, Set<Good> goods, Set<BundleBid> acceptedBids) {
        this(value, goods.stream().collect(Collectors.toMap(good -> good, good -> 1)), acceptedBids);
    }

    public PotentialCoalition getPotentialCoalition(Bidder bidder) {
        return new PotentialCoalition(goods, bidder, value);
    }

}
