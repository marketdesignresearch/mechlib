package org.marketdesignresearch.mechlib.core.bid.bundle;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.BundleEntry;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Collection of BundleValueBids per bidder (i.e. holds all bids of all bidders in an auction).
 * 
 * @author Manuel Beyeler
 *
 * @param <T> type of bids
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public abstract class BundleValueBids<T extends BundleValueBid<? extends BundleExactValuePair>> extends Bids<T> {

	public BundleValueBids() {
		this(new LinkedHashMap<>());
	}

	public BundleValueBids(Map<Bidder, T> bidderBidMap) {
		super(bidderBidMap);
	}

	public Collection<Good> getGoods() {
		Set<Good> goods = new LinkedHashSet<>();
		getBids().forEach(bid -> bid.getBundleBids().forEach(bbid -> goods.addAll(bbid.getBundle().getBundleEntries()
				.stream().map(BundleEntry::getGood).collect(Collectors.toCollection(LinkedHashSet::new)))));
		return goods;
	}

	public int getDemand(Good good) {
		int demand = 0;
		for (T bid : this.getBidMap().values()) {
			demand += bid.getBundleBids().stream().mapToInt(bb -> bb.countGood(good)).max().orElse(0);
		}
		return demand;
	}

	/**
	 *
	 * @param bidder to be removed
	 * @return New bids not including the bid of the specified bidder
	 */
	public abstract BundleValueBids<T> without(Bidder bidder);

	/**
	 * 
	 * @param bidders bids for the respective bidders
	 * @return New bids only including specified bidders
	 */
	public abstract BundleValueBids<T> only(Set<UUID> bidders);

	/**
	 * @param bidders to be included
	 * @return New bids consisting only of the bids of the specified bidders
	 */
	public abstract BundleValueBids<T> of(Set<Bidder> bidders);

	/**
	 * @param other BundleValueBids.
	 * @return a new BundleValueBids containing all bids of this and other.
	 */
	public abstract BundleValueBids<T> join(BundleValueBids<?> other);

	public abstract BundleValueBids<T> reducedBy(Outcome outcome);

	/**
	 * @param factor multiplication factor
	 * @return a new BundleValueBids where each (bundle) value is multiplied with the given multiplication factor.
	 */
	public abstract BundleValueBids<T> multiply(BigDecimal factor);

	public SingleItemBids getBidsPerSingleGood(Good good) {
		if (!getGoods().contains(good))
			return new SingleItemBids(new BundleExactValueBids());
		Map<Bidder, BundleExactValueBid> bidsPerGood = new LinkedHashMap<>();
		for (Entry<Bidder, T> entry : this.getBidMap().entrySet()) {
			Set<BundleExactValuePair> bundleBids = entry.getValue().getBundleBids().stream()
					.filter(bbid -> bbid.getBundle().isSingleGood())
					.filter(bbid -> bbid.getBundle().getSingleGood().equals(good))
					.collect(Collectors.toCollection(LinkedHashSet::new));
			if (!bundleBids.isEmpty())
				bidsPerGood.put(entry.getKey(), new BundleExactValueBid(bundleBids));
		}
		return new SingleItemBids(new BundleExactValueBids(bidsPerGood));
	}
}
