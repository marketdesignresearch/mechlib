package org.marketdesignresearch.mechlib.domain.bid;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.marketdesignresearch.mechlib.domain.BundleEntry;
import org.marketdesignresearch.mechlib.domain.Good;
import org.marketdesignresearch.mechlib.domain.bidder.Bidder;
import org.marketdesignresearch.mechlib.domain.bidder.ORBidder;
import org.marketdesignresearch.mechlib.domain.bidder.XORBidder;
import org.marketdesignresearch.mechlib.domain.bidder.value.Value;
import org.marketdesignresearch.mechlib.mechanisms.AuctionResult;
import org.marketdesignresearch.mechlib.strategy.Strategy;

import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@ToString
public class Bids implements Iterable<Entry<Bidder, Bid>> {
    @Getter
    private final Map<Bidder, Bid> bidMap;

    public Bids() {
        this(new LinkedHashMap<>());
    }

    public boolean setBid(Bidder bidder, Bid bid) {
        return bidMap.put(bidder, bid) == null;
    }

    public Set<Bidder> getBidders() {
        return bidMap.keySet();
    }

    public Collection<Bid> getBids() {
        return bidMap.values();
    }

    public Collection<Good> getGoods() {
        Set<Good> goods = new HashSet<>();
        getBids().forEach(bid -> bid.getBundleBids().forEach(bbid -> goods.addAll(bbid.getBundle().getBundleEntries().stream().map(BundleEntry::getGood).collect(Collectors.toSet()))));
        return goods;
    }

    /**
     *
     * @param bidder to be removed
     * @return New bids not including the bid of the specified bidder
     */
    public Bids without(Bidder bidder) {
        Map<Bidder, Bid> newBidderBidMap = new HashMap<>(bidMap);
        newBidderBidMap.remove(bidder);
        return new Bids(newBidderBidMap);
    }

    /**
     *
     * @param bidders to be included
     * @return New bids consisting only of the bids of the specified bidders
     */
    public Bids of(Set<Bidder> bidders) {
        Map<Bidder, Bid> newBidderBidMap = new HashMap<>(Maps.filterKeys(bidMap, bidders::contains));
        return new Bids(newBidderBidMap);
    }

    public Bid getBid(Bidder bidder) {
        return bidMap.get(bidder);
    }

    @Override
    public Iterator<Entry<Bidder, Bid>> iterator() {
        return bidMap.entrySet().iterator();
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
    public static Bids fromXORBidders(List<XORBidder> bidders) {
        return fromXORBidders(bidders, Strategy.TRUTHFUL::apply);
    }

    public static Bids fromXORBidders(List<XORBidder> bidders, Function<Value, Bid> operator) {
        Map<Bidder, Bid> bidMap = new HashMap<>();
        for (XORBidder bidder : bidders) {
            bidMap.put(bidder, operator.apply(bidder.getValue()));
        }
        return new Bids(bidMap);
    }

    public static Bids fromOBidders(List<ORBidder> bidders) {
        return fromORBidders(bidders, Strategy.TRUTHFUL::apply);
    }

    public static Bids fromORBidders(List<ORBidder> bidders, Function<Value, Bid> operator) {
        Map<Bidder, Bid> bidMap = new HashMap<>();
        for (ORBidder bidder : bidders) {
            bidMap.put(bidder, operator.apply(bidder.getValue()));
        }
        return new Bids(bidMap);
    }

    // TODO: Does not work with OR*
    /**
     *
     * @return New bids, but reduced by the payoff of
     *         auctionResult
     */
    public Bids reducedBy(AuctionResult auctionResult) {
        Bids newBids = new Bids();
        for (Map.Entry<Bidder, Bid> entry : getBidMap().entrySet()) {
            BigDecimal payoff = auctionResult.payoffOf(entry.getKey());
            newBids.setBid(entry.getKey(), entry.getValue().reducedBy(payoff));
        }
        return newBids;
    }
}
