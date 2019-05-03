package org.marketdesignresearch.mechlib.domain;

import org.marketdesignresearch.mechlib.domain.bidder.SimpleBidder;
import org.marketdesignresearch.mechlib.domain.bidder.Value;
import org.marketdesignresearch.mechlib.strategy.Strategy;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;

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

    public Bids without(Bidder bidder) {
        Map<Bidder, Bid> newBidderBidMap = new HashMap<>(bidMap);
        newBidderBidMap.remove(bidder);
        return new Bids(newBidderBidMap);
    }

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
    public static Bids fromSimpleBidders(Set<SimpleBidder> bidders) {
        return fromSimpleBidders(bidders, Strategy.TRUTHFUL::apply);
    }

    public static Bids fromSimpleBidders(Set<SimpleBidder> bidders, Function<Value, Bid> operator) {
        Map<Bidder, Bid> bidMap = new HashMap<>();
        for (SimpleBidder simpleBidder : bidders) {
            bidMap.put(simpleBidder, operator.apply(simpleBidder.getValue()));
        }
        return new Bids(bidMap);
    }
}
