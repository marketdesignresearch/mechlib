package org.marketdesignresearch.mechlib.core.bid;

import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.ToString;
import org.marketdesignresearch.mechlib.core.BundleBid;
import org.marketdesignresearch.mechlib.core.BundleEntry;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.bidder.ORBidder;
import org.marketdesignresearch.mechlib.core.bidder.XORBidder;
import org.marketdesignresearch.mechlib.core.bidder.strategy.Strategy;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.ValueFunction;

import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

@ToString
public class Bids {

    private final Map<UUID, Bid> bidMap = new HashMap<>();
    @Getter
    private final Set<Bidder> bidders = new HashSet<>();

    public Bids() {}

    public Bids(Map<Bidder, Bid> bidderBidMap) {
        bidders.addAll(bidderBidMap.keySet());
        bidderBidMap.forEach((k, v) -> this.bidMap.put(k.getId(), v));
    }

    public boolean setBid(Bidder bidder, Bid bid) {
        bidders.add(bidder);
        return bidMap.put(bidder.getId(), bid) == null;
    }

    public Collection<Bid> getBids() {
        return bidMap.values();
    }

    public Map<Bidder, Bid> getBidMap() {
        HashMap<Bidder, Bid> map = new HashMap<>();
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
        for (Bid bid : bidMap.values()) {
            demand += bid.getBundleBids().stream().mapToInt(bb -> bb.countGood(good)).max().orElse(0);
        }
        return demand;
    }

    public SingleItemBids getBidsPerSingleGood(Good good) {
        if (!getGoods().contains(good)) return new SingleItemBids(new Bids());
        Map<Bidder, Bid> bidsPerGood = new HashMap<>();
        for (Entry<UUID, Bid> entry : bidMap.entrySet()) {
            Set<BundleBid> bundleBids = entry.getValue().getBundleBids().stream().filter(bbid -> bbid.getBundle().isSingleGood()).filter(bbid -> bbid.getBundle().getSingleGood().equals(good)).collect(Collectors.toSet());
            if (!bundleBids.isEmpty()) bidsPerGood.put(getBidder(entry.getKey()), new Bid(bundleBids));
        }
        return new SingleItemBids(new Bids(bidsPerGood));
    }

    private Bidder getBidder(UUID id) {
        return getBidders().stream().filter(b -> b.getId().equals(id)).findAny().orElseThrow(NoSuchElementException::new);
    }

    /**
     *
     * @param bidder to be removed
     * @return New bids not including the bid of the specified bidder
     */
    public Bids without(Bidder bidder) {
        Map<Bidder, Bid> newBidderBidMap = new HashMap<>();
        bidMap.forEach((k, v) -> {
            Bidder b = getBidder(k);
            if (!b.equals(bidder)) newBidderBidMap.put(b, v);
        });
        return new Bids(newBidderBidMap);
    }

    /**
     *
     * @param bidders to be included
     * @return New bids consisting only of the bids of the specified bidders
     */
    public Bids of(Set<Bidder> bidders) {
        Map<Bidder, Bid> newBidderBidMap = new HashMap<>();
        bidMap.forEach((k, v) -> {
            Bidder b = getBidder(k);
            if (bidders.contains(b)) newBidderBidMap.put(b, v);
        });
        return new Bids(newBidderBidMap);
    }

    public Bid getBid(Bidder bidder) {
        return bidMap.getOrDefault(bidder.getId(), new Bid());
    }

    public Bids join(Bids other) {
        Bids result = new Bids();
        Set<Bidder> bidders = Sets.union(getBidders(), other.getBidders());
        bidders.forEach(b -> {
            Bid joined = new Bid();
            if (getBid(b) != null) getBid(b).getBundleBids().forEach(joined::addBundleBid);
            if (other.getBid(b) != null) other.getBid(b).getBundleBids().forEach(joined::addBundleBid);
            result.setBid(b, joined);
        });
        return result;
    }

    /**
     * Gives truthful bids
     */
    public static Bids fromXORBidders(List<? extends XORBidder> bidders) {
        return fromXORBidders(bidders, Strategy.TRUTHFUL::apply);
    }

    public static Bids fromXORBidders(List<? extends XORBidder> bidders, Function<ValueFunction, Bid> operator) {
        Map<Bidder, Bid> bidMap = new HashMap<>();
        for (XORBidder bidder : bidders) {
            bidMap.put(bidder, operator.apply(bidder.getValue()));
        }
        return new Bids(bidMap);
    }

    public static Bids fromORBidders(List<? extends ORBidder> bidders) {
        return fromORBidders(bidders, Strategy.TRUTHFUL::apply);
    }

    public static Bids fromORBidders(List<? extends ORBidder> bidders, Function<ValueFunction, Bid> operator) {
        Map<Bidder, Bid> bidMap = new HashMap<>();
        for (ORBidder bidder : bidders) {
            bidMap.put(bidder, operator.apply(bidder.getValue()));
        }
        return new Bids(bidMap);
    }

    // TODO: Does not work with OR*
    /**
     *
     * @return New bids, but reduced by the payoff of outcome
     *
     */
    public Bids reducedBy(Outcome outcome) {
        Bids newBids = new Bids();
        for (Map.Entry<Bidder, Bid> entry : getBidMap().entrySet()) {
            BigDecimal payoff = outcome.payoffOf(entry.getKey());
            newBids.setBid(entry.getKey(), entry.getValue().reducedBy(payoff));
        }
        return newBids;
    }

    public boolean isEmpty() {
        return bidMap.isEmpty() || bidMap.values().stream().allMatch(bid -> bid.getBundleBids().isEmpty());
    }
}
