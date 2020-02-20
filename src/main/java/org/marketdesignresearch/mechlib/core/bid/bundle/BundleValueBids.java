package org.marketdesignresearch.mechlib.core.bid.bundle;

import com.google.common.collect.Sets;
import lombok.*;

import org.marketdesignresearch.mechlib.core.BundleEntry;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.bidder.ORBidder;
import org.marketdesignresearch.mechlib.core.bidder.XORBidder;
import org.marketdesignresearch.mechlib.core.bidder.strategy.Strategy;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.ValueFunction;
import org.springframework.data.annotation.PersistenceConstructor;

import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@PersistenceConstructor}))
@ToString @EqualsAndHashCode
public class BundleValueBids<T extends BundleValuePair> implements Bids{

    private final Map<UUID, BundleValueBid<T>> bidMap;
    @Getter
    private final Set<Bidder> bidders;

    public BundleValueBids() {
        this(new HashMap<>());
    }

    public BundleValueBids(Map<Bidder, BundleValueBid<T>> bidderBidMap) {
        bidMap = new HashMap<>();
        bidders = new HashSet<>(bidderBidMap.keySet());
        bidderBidMap.forEach((k, v) -> this.bidMap.put(k.getId(), v));
    }

    public boolean setBid(Bidder bidder, BundleValueBid<T> bid) {
        bidders.add(bidder);
        return bidMap.put(bidder.getId(), bid) == null;
    }

    public Collection<BundleValueBid<T>> getBids() {
        return bidMap.values();
    }

    public Map<Bidder, BundleValueBid<T>> getBidMap() {
        HashMap<Bidder, BundleValueBid<T>> map = new HashMap<>();
        bidMap.forEach((k, v) -> map.put(getBidder(k), v));
        return map;
    }

    public Collection<Good> getGoods() {
        Set<Good> goods = new HashSet<>();
        getBids().forEach(bid -> bid.getBundleBids().forEach(bbid -> goods.addAll(bbid.getBundle().getBundleEntries().stream().map(BundleEntry::getGood).collect(Collectors.toSet()))));
        return goods;
    }

    public int getDemand(Good good) {
        int demand = 0;
        for (BundleValueBid<T> bid : bidMap.values()) {
            demand += bid.getBundleBids().stream().mapToInt(bb -> bb.countGood(good)).max().orElse(0);
        }
        return demand;
    }

    // TODO recheck cast
    @SuppressWarnings("unchecked")
	public SingleItemBids getBidsPerSingleGood(Good good) {
        if (!getGoods().contains(good)) return new SingleItemBids(new BundleValueBids<BundleValuePair>());
        Map<Bidder, BundleValueBid<BundleValuePair>> bidsPerGood = new HashMap<>();
        for (Entry<UUID, BundleValueBid<T>> entry : bidMap.entrySet()) {
            Set<T> bundleBids = entry.getValue().getBundleBids().stream().filter(bbid -> bbid.getBundle().isSingleGood()).filter(bbid -> bbid.getBundle().getSingleGood().equals(good)).collect(Collectors.toSet());
            if (!bundleBids.isEmpty()) bidsPerGood.put(getBidder(entry.getKey()), new BundleValueBid<BundleValuePair>((Set<BundleValuePair>) bundleBids));
        }
        return new SingleItemBids(new BundleValueBids<BundleValuePair>(bidsPerGood));
    }

    private Bidder getBidder(UUID id) {
        return getBidders().stream().filter(b -> b.getId().equals(id)).findAny().orElseThrow(NoSuchElementException::new);
    }

    /**
     *
     * @param bidder to be removed
     * @return New bids not including the bid of the specified bidder
     */
    public BundleValueBids<T> without(Bidder bidder) {
        Map<Bidder, BundleValueBid<T>> newBidderBidMap = new HashMap<>();
        bidMap.forEach((k, v) -> {
            Bidder b = getBidder(k);
            if (!b.equals(bidder)) newBidderBidMap.put(b, v);
        });
        return new BundleValueBids<T>(newBidderBidMap);
    }

    /**
     *
     * @param bidders to be included
     * @return New bids consisting only of the bids of the specified bidders
     */
    public BundleValueBids<T> of(Set<Bidder> bidders) {
        Map<Bidder, BundleValueBid<T>> newBidderBidMap = new HashMap<>();
        bidMap.forEach((k, v) -> {
            Bidder b = getBidder(k);
            if (bidders.contains(b)) newBidderBidMap.put(b, v);
        });
        return new BundleValueBids<T>(newBidderBidMap);
    }

    public BundleValueBid<T> getBid(Bidder bidder) {
        return bidMap.getOrDefault(bidder.getId(), new BundleValueBid<T>());
    }

    public BundleValueBids<T> join(BundleValueBids<T> other) {
        BundleValueBids<T> result = new BundleValueBids<T>();
        Set<Bidder> bidders = Sets.union(getBidders(), other.getBidders());
        bidders.forEach(b -> {
            BundleValueBid<T> joined = new BundleValueBid<>();
            if (getBid(b) != null) getBid(b).getBundleBids().forEach(joined::addBundleBid);
            if (other.getBid(b) != null) other.getBid(b).getBundleBids().forEach(joined::addBundleBid);
            result.setBid(b, joined);
        });
        return result;
    }

    /**
     * Gives truthful bids
     */
    public static BundleValueBids<BundleValuePair> fromXORBidders(List<? extends XORBidder> bidders) {
        return fromXORBidders(bidders, Strategy.TRUTHFUL::apply);
    }

    public static BundleValueBids<BundleValuePair> fromXORBidders(List<? extends XORBidder> bidders, Function<ValueFunction, BundleValueBid<BundleValuePair>> operator) {
        Map<Bidder, BundleValueBid<BundleValuePair>> bidMap = new HashMap<>();
        for (XORBidder bidder : bidders) {
            bidMap.put(bidder, operator.apply(bidder.getValue()));
        }
        return new BundleValueBids<BundleValuePair>(bidMap);
    }

    public static BundleValueBids<BundleValuePair> fromORBidders(List<? extends ORBidder> bidders) {
        return fromORBidders(bidders, Strategy.TRUTHFUL::apply);
    }

    public static BundleValueBids<BundleValuePair> fromORBidders(List<? extends ORBidder> bidders, Function<ValueFunction, BundleValueBid<BundleValuePair>> operator) {
        Map<Bidder, BundleValueBid<BundleValuePair>> bidMap = new HashMap<>();
        for (ORBidder bidder : bidders) {
            bidMap.put(bidder, operator.apply(bidder.getValue()));
        }
        return new BundleValueBids<BundleValuePair>(bidMap);
    }

    // TODO: Does not work with OR*
    /**
     *
     * @return New bids, but reduced by the payoff of outcome
     *
     */
    public BundleValueBids<T> reducedBy(Outcome outcome) {
        BundleValueBids<T> newBids = new BundleValueBids<>();
        for (Map.Entry<Bidder, BundleValueBid<T>> entry : getBidMap().entrySet()) {
            BigDecimal payoff = outcome.payoffOf(entry.getKey());
            newBids.setBid(entry.getKey(), entry.getValue().reducedBy(payoff));
        }
        return newBids;
    }

    public boolean isEmpty() {
        return bidMap.isEmpty() || bidMap.values().stream().allMatch(bid -> bid.getBundleBids().isEmpty());
    }
}
