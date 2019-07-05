package org.marketdesignresearch.mechlib.domain;

import com.google.common.collect.Sets;
import org.marketdesignresearch.mechlib.domain.bid.Bids;
import org.marketdesignresearch.mechlib.domain.bidder.Bidder;
import org.marketdesignresearch.mechlib.mechanisms.MetaInfoResult;
import org.marketdesignresearch.mechlib.mechanisms.MetaInfo;
import org.marketdesignresearch.mechlib.mechanisms.ccg.constraintgeneration.PotentialCoalition;
import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class represents the Allocation after a WinnerDetermination. It contains
 * the total welfare of the Allocation as well as a Map of Bidders to their
 * tradesMap.</p> Each winning bidder has exactly one associated trade. Non winning
 * bidders are not included
 *
 * @author Benedikt Buenz
 */
@AllArgsConstructor
@EqualsAndHashCode(of = "tradesMap")
@ToString
public class Allocation implements MetaInfoResult {
    public static final Allocation EMPTY_ALLOCATION = new Allocation(ImmutableMap.of(), new Bids(new HashMap<>()), new MetaInfo());
    @Getter
    private final BigDecimal totalAllocationValue; // TODO: Is that ever different than the sum of the bidder allocation's values?
    @Getter
    private final Map<? extends Bidder, BidderAllocation> tradesMap; // The Map only includes winning bidders
    @Getter
    private final Bids bids;
    @Getter
    private final MetaInfo metaInfo;
    private Set<PotentialCoalition> coalitions;

    public Allocation(Map<? extends Bidder, BidderAllocation> tradesMap, Bids bids, MetaInfo metaInfo) {
        this(tradesMap, bids, metaInfo, null);
    }
    public Allocation(BigDecimal totalAllocationValue, Map<? extends Bidder, BidderAllocation> tradesMap, Bids bids, MetaInfo metaInfo) {
        this(totalAllocationValue, tradesMap, bids, metaInfo, null);
    }

    public Allocation(Map<? extends Bidder, BidderAllocation> tradesMap, Bids bids, MetaInfo metaInfo, Set<PotentialCoalition> potentialCoalitions) {
        this(tradesMap.values().stream().map(BidderAllocation::getValue).reduce(BigDecimal.ZERO, BigDecimal::add), tradesMap, bids, metaInfo, potentialCoalitions);
    }

    public BidderAllocation allocationOf(Bidder bidder) {
        return tradesMap.getOrDefault(bidder, BidderAllocation.ZERO_ALLOCATION);
    }

    public Set<? extends Bidder> getWinners() {
        return tradesMap.keySet();
    }

    public boolean isWinner(Bidder bidder) {
        return tradesMap.containsKey(bidder);
    }

    public Set<PotentialCoalition> getPotentialCoalitions() {
        if (coalitions == null) {
            Set<PotentialCoalition> coalitions = new HashSet<>(tradesMap.size());
            coalitions.addAll(getWinners().stream()
                    .map(bidder -> allocationOf(bidder).getPotentialCoalition(bidder))
                    .filter(pc -> pc.getValue().signum() > 0)
                    .collect(Collectors.toList()));
            this.coalitions = coalitions;
        }
        return this.coalitions;
    }

    public Allocation merge(Allocation other) {
        Map<Bidder, BidderAllocation> tradesMap = new HashMap<>();
        for (Bidder bidder : Sets.union(getWinners(), other.getWinners())) {
            tradesMap.put(bidder, allocationOf(bidder).merge(other.allocationOf(bidder)));
        }
        return new Allocation(tradesMap, getBids().join(other.getBids()), getMetaInfo().join(other.getMetaInfo()));
    }

    public Allocation getAllocationWithTrueValues() {
        Map<Bidder, BidderAllocation> map = new HashMap<>();
        tradesMap.forEach((k, v) -> map.put(k, new BidderAllocation(k.getValue(v.getBundle()), v.getBundle(), new HashSet<>())));
        return new Allocation(map, new Bids(), new MetaInfo());
    }
}
