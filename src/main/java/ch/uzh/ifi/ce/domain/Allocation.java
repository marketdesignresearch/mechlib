package ch.uzh.ifi.ce.domain;

import ch.uzh.ifi.ce.mechanisms.MechanismResult;
import ch.uzh.ifi.ce.mechanisms.MetaInfo;
import ch.uzh.ifi.ce.mechanisms.ccg.constraintgeneration.PotentialCoalition;
import com.google.common.collect.ImmutableMap;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class represents the Allocation after a WinnerDetermination. It contains
 * the total welfare of the Allocation as well as a Map of Bidders to their
 * trades.</p> Each winning bidder has exactly one associated trade. Non winning
 * bidders are not included
 *
 * @author Benedikt Buenz
 */
public class Allocation implements MechanismResult {
    public static final Allocation EMPTY_ALLOCATION = new Allocation(ImmutableMap.of(), new Bids(new HashMap<>()), new MetaInfo());
    private final BigDecimal totalValue;
    private final Map<Bidder, BidderAllocation> trades;
    private final Bids bids;
    private final MetaInfo metaInfo;
    private Set<PotentialCoalition> coalitions = null;

    /**
     *
     */

    public Allocation(Map<Bidder, BidderAllocation> trades, Bids bids, MetaInfo metaInfo) {
        this(trades, bids, metaInfo, null);
    }
    public Allocation(BigDecimal totalValue, Map<Bidder, BidderAllocation> trades, Bids bids, MetaInfo metaInfo) {
        this(totalValue,trades, bids, metaInfo, null);
    }

    public Allocation(Map<Bidder, BidderAllocation> trades, Bids bids, MetaInfo metaInfo, Set<PotentialCoalition> potentialCoalitions) {
        this.totalValue = trades.values().stream().map(BidderAllocation::getValue).reduce(BigDecimal.ZERO, BigDecimal::add);
        this.bids = bids;
        this.trades = trades;
        this.metaInfo = metaInfo;
        this.coalitions = potentialCoalitions;
    }

    public Allocation(BigDecimal totalValue, Map<Bidder, BidderAllocation> trades, Bids bids, MetaInfo metaInfo, Set<PotentialCoalition> potentialCoalitions) {
        this.totalValue = totalValue;
        this.bids = bids;
        this.trades = trades;
        this.metaInfo = metaInfo;
        this.coalitions = potentialCoalitions;
    }

    public BigDecimal getTotalAllocationValue() {
        return totalValue;
    }

    /**
     * The Map only includes winning bidders
     *
     * @return
     */
    public Map<Bidder, BidderAllocation> getTradesMap() {
        return trades;
    }

    public boolean isWinner(Bidder bidder) {
        return trades.containsKey(bidder);
    }

    public BidderAllocation allocationOf(Bidder bidder) {
        return trades.getOrDefault(bidder, BidderAllocation.ZERO_ALLOCATION);
    }

    public Set<Bidder> getWinners() {
        return trades.keySet();
    }

    public Bids getBids() {
        return bids;
    }

    public Set<PotentialCoalition> getPotentialCoalitions() {
        if (coalitions == null) {

            Set<PotentialCoalition> coalitions = new HashSet<>(trades.size());
            coalitions.addAll(getWinners().stream()
                    .map(bidder -> allocationOf(bidder).getPotentialCoalition(bidder))
                    .filter(pc -> pc.getValue().signum() > 0)
                    .collect(Collectors.toList()));
            this.coalitions = coalitions;
        }
        return this.coalitions;

    }

    @Override
    public int hashCode() {
        return trades.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Allocation otherAllocation = (Allocation) obj;
        return trades.equals(otherAllocation.trades);
    }

    @Override
    public MetaInfo getMetaInfo() {
        return metaInfo;
    }

    @Override
    public String toString() {
        return "Allocation[totalValue=" + totalValue + ", trades=" + trades + "\nmetaInfo=" + metaInfo + "]";

    }
}
