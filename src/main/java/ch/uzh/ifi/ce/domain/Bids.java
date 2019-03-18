package ch.uzh.ifi.ce.domain;

import com.google.common.collect.Maps;

import java.util.*;
import java.util.Map.Entry;

public class Bids implements Iterable<Entry<Bidder, Bid>> {
    private final Map<Bidder, Bid> bids;

    public Bids(Map<Bidder, Bid> bids) {
        this.bids = bids;
    }

    public Bids() {
        this(new LinkedHashMap<>());
    }

    public Map<Bidder, Bid> getBidMap() {
        return bids;
    }

    public boolean setBid(Bidder bidder, Bid bid) {
        return bids.put(bidder, bid) == null;
    }

    @Override
    public Iterator<Entry<Bidder, Bid>> iterator() {
        return bids.entrySet().iterator();
    }

    public Set<Bidder> getBidders() {
        return bids.keySet();
    }

    public Collection<Bid> getBids() {
        return bids.values();
    }

    public Bids without(Bidder bidder) {
        Map<Bidder, Bid> newBidderBidMap = new HashMap<>(bids);
        newBidderBidMap.remove(bidder);
        return new Bids(newBidderBidMap);
    }

    public Bids of(Set<Bidder> bidders) {

        Map<Bidder, Bid> newBidderBidMap = new HashMap<>(Maps.filterKeys(bids, bidders::contains));
        return new Bids(newBidderBidMap);
    }

    public Bid getBid(Bidder bidder) {
        return bids.get(bidder);
    }

    public boolean contains(String id) {
        Bidder searchBidder = new Bidder(id);
        return bids.containsKey(searchBidder);
    }

    @Override
    public String toString() {
        return "Bids[bidMap=" + bids + "]";
    }
}
