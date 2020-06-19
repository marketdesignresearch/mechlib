package org.marketdesignresearch.mechlib.core.bid;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.springframework.data.annotation.PersistenceConstructor;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({ @PersistenceConstructor }))
public abstract class Bids<T extends Bid> {

	private final Map<UUID, T> bidMap;
	@Getter
	private final Set<Bidder> bidders;

	public Bids() {
		this(new HashMap<>());
	}

	public Bids(Map<? extends Bidder, T> bidderBidMap) {
		bidMap = new HashMap<>();
		bidders = new HashSet<>(bidderBidMap.keySet());
		bidderBidMap.forEach((k, v) -> this.bidMap.put(k.getId(), v));
	}

	public boolean setBid(Bidder bidder, T bid) {
		bidders.add(bidder);
		return bidMap.put(bidder.getId(), bid) == null;
	}

	public Collection<T> getBids() {
		return bidMap.values();
	}

	public Map<Bidder, T> getBidMap() {
		HashMap<Bidder, T> map = new HashMap<>();
		bidMap.forEach((k, v) -> map.put(getBidder(k), v));
		return map;
	}

	public T getBid(Bidder bidder) {
		return bidMap.getOrDefault(bidder.getId(), this.createEmptyBid());
	}

	protected abstract T createEmptyBid();

	private Bidder getBidder(UUID id) {
		return this.bidders.stream().filter(b -> b.getId().equals(id)).findAny().orElse(null);
	}

	public boolean isEmpty() {
		return bidMap.isEmpty() || this.getBidMap().values().stream().allMatch(Bid::isEmpty);
	}
}
