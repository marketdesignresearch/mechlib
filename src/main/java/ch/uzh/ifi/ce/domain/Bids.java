package ch.uzh.ifi.ce.domain;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.*;
import java.util.Map.Entry;

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

    public boolean contains(String id) {
        Bidder searchBidder = new Bidder(id);
        return bidMap.containsKey(searchBidder);
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
}
