package org.marketdesignresearch.mechlib.core;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.metainfo.MetaInfo;
import org.marketdesignresearch.mechlib.metainfo.MetaInfoResult;
import org.marketdesignresearch.mechlib.outcomerules.ccg.constraintgeneration.PotentialCoalition;
import org.springframework.data.annotation.PersistenceConstructor;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * This class represents the Allocation after a WinnerDetermination. It contains
 * the total welfare of the Allocation as well as a Map of Bidders to their
 * tradesMap.
 * 
 * Each winning bidder has exactly one associated trade. Non winning bidders are
 * not included
 *
 * @author Benedikt Buenz
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({ @PersistenceConstructor }))
@EqualsAndHashCode
@ToString
public class Allocation implements MetaInfoResult {
	public static final Allocation EMPTY_ALLOCATION = new Allocation(ImmutableMap.of(),
			new BundleExactValueBids(new LinkedHashMap<>()), new MetaInfo());
	@Getter
	private final BigDecimal totalAllocationValue; // TODO: Is that ever different than the sum of the bidder
													// allocation's values?
	@Getter
	private final BigDecimal trueSocialWelfare;

	private final Map<UUID, BidderAllocation> tradesMap; // The Map only includes winning bidders
	@Getter
	@EqualsAndHashCode.Exclude
	private final BundleValueBids<?> bids;
	@Getter
	@EqualsAndHashCode.Exclude
	private final MetaInfo metaInfo;
	@Getter
	private final Set<? extends Bidder> winners;
	@EqualsAndHashCode.Exclude
	private Set<PotentialCoalition> coalitions;

	public Allocation(Map<? extends Bidder, BidderAllocation> tradesMap, BundleValueBids<?> bids, MetaInfo metaInfo) {
		this(tradesMap, bids, metaInfo, null);
	}

	public Allocation(BigDecimal totalAllocationValue, Map<? extends Bidder, BidderAllocation> tradesMap,
			BundleValueBids<?> bids, MetaInfo metaInfo) {
		this(totalAllocationValue, tradesMap, bids, metaInfo, null);
	}

	public Allocation(Map<? extends Bidder, BidderAllocation> tradesMap, BundleValueBids<?> bids, MetaInfo metaInfo,
			Set<PotentialCoalition> potentialCoalitions) {
		this(tradesMap.values().stream().map(BidderAllocation::getValue).reduce(BigDecimal.ZERO, BigDecimal::add),
				tradesMap, bids, metaInfo, potentialCoalitions);
	}

	public Allocation(BigDecimal totalAllocationValue, Map<? extends Bidder, BidderAllocation> tradesMap,
			BundleValueBids<?> bids, MetaInfo metaInfo, Set<PotentialCoalition> coalitions) {
		this.totalAllocationValue = totalAllocationValue;
		this.trueSocialWelfare = tradesMap.entrySet().stream().map(e -> e.getKey().getValue(e.getValue().getBundle(), true))
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		Map<UUID, BidderAllocation> map = new LinkedHashMap<>();
		tradesMap.forEach((bidder, bidderAllocation) -> map.put(bidder.getId(), bidderAllocation));
		this.tradesMap = ImmutableMap.copyOf(map);
		this.winners = ImmutableSet.copyOf(tradesMap.keySet());
		this.bids = bids;
		this.metaInfo = metaInfo;
		this.coalitions = coalitions;
	}

	public BidderAllocation allocationOf(Bidder bidder) {
		return tradesMap.getOrDefault(bidder.getId(), BidderAllocation.ZERO_ALLOCATION);
	}

	public Map<Bidder, BidderAllocation> getTradesMap() {
		Map<Bidder, BidderAllocation> map = new LinkedHashMap<>();
		tradesMap.forEach((k, v) -> map.put(getBidder(k), v));
		return map;
	}

	public boolean isWinner(Bidder bidder) {
		return tradesMap.containsKey(bidder.getId());
	}

	public Bundle getAllocatedBundle() {
		return this.tradesMap.values().stream().map(BidderAllocation::getBundle).reduce(Bundle::merge)
				.orElse(Bundle.EMPTY);
	}

	public Set<PotentialCoalition> getPotentialCoalitions() {
		if (coalitions == null) {
			Set<PotentialCoalition> coalitions = new LinkedHashSet<>(tradesMap.size());
			coalitions.addAll(getWinners().stream().map(bidder -> allocationOf(bidder).getPotentialCoalition(bidder))
					.filter(pc -> pc.getValue().signum() > 0).collect(Collectors.toList()));
			this.coalitions = coalitions;
		}
		return this.coalitions;
	}

	public Allocation merge(Allocation other) {
		Map<Bidder, BidderAllocation> tradesMap = new LinkedHashMap<>();
		for (Bidder bidder : Sets.union(getWinners(), other.getWinners())) {
			tradesMap.put(bidder, allocationOf(bidder).merge(other.allocationOf(bidder)));
		}
		return new Allocation(tradesMap, getBids().join(other.getBids()), getMetaInfo().join(other.getMetaInfo()));
	}

	public Allocation getAllocationWithTrueValues() {
		Map<Bidder, BidderAllocation> map = new LinkedHashMap<>();
		tradesMap.forEach((k, v) -> map.put(getBidder(k),
				new BidderAllocation(getBidder(k).getValue(v.getBundle()), v.getBundle(), new LinkedHashSet<>())));
		return new Allocation(map, new BundleExactValueBids(), new MetaInfo());
	}

	private Bidder getBidder(UUID id) {
		return winners.stream().filter(b -> b.getId().equals(id)).findAny().orElseThrow(NoSuchElementException::new);
	}
}
