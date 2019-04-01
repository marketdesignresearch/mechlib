package ch.uzh.ifi.ce.domain;

import ch.uzh.ifi.ce.strategy.Strategy;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

@RequiredArgsConstructor
public class Values implements Iterable<Entry<Bidder, Value>>, Serializable {
    private static final long serialVersionUID = -580085158734188659L;

    @Getter
    private final Map<Bidder, Value> valueMap;

    public Values() {
        this(new HashMap<>());
    }

    public boolean addValue(Bidder bidder, Value combinatorialValue) {
        return valueMap.put(bidder, combinatorialValue) == null;
    }

    public Values subValue(Collection<Bidder> bidders) {
        Map<Bidder, Value> newValueMap = new HashMap<>(bidders.size());
        bidders.forEach(b -> newValueMap.put(b, getValueMap().get(b)));
        return new Values(newValueMap);
    }

    @Override
    public Iterator<Entry<Bidder, Value>> iterator() {
        return getValueMap().entrySet().iterator();
    }

    public Set<Bidder> getBidders() {
        return getValueMap().keySet();
    }

    public Collection<Value> getValues() {
        return getValueMap().values();
    }

    public Value getValue(Bidder bidder) {
        return getValueMap().get(bidder);
    }

    public boolean contains(String id) {
        Bidder searchBidder = new Bidder(id);
        return getValueMap().containsKey(searchBidder);
    }

    public Bids toBids() {
        return new Bids(new HashMap<>(Maps.transformValues(getValueMap(), Strategy.TRUTHFUL::apply)));
    }

}
