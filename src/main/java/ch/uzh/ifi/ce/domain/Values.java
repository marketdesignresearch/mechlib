package ch.uzh.ifi.ce.domain;

import ch.uzh.ifi.ce.strategy.Strategy;
import com.google.common.collect.Maps;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

public class Values implements Iterable<Entry<Bidder, Value>>, Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -580085158734188659L;
    private final Map<Bidder, Value> values;

    public Values(Map<Bidder, Value> values) {

        this.values = values;
    }

    public Values() {
        this(new HashMap<>());
    }

    /*
     * (non-Javadoc)
     * 
     * @see ch.uzh.ifi.ce.cca.domain.Values#getValueMap()
     */
    public Map<Bidder, Value> getValueMap() {
        return values;
    }

    public boolean addValue(Bidder bidder, Value combinatorialValue) {
        return values.put(bidder, combinatorialValue) == null;
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * ch.uzh.ifi.ce.cca.domain.Values#getValue(ch.uzh.ifi.ce.cca.domain.Bidder)
     */

    public Value getValue(Bidder bidder) {
        return getValueMap().get(bidder);
    }


    /*
     * (non-Javadoc)
     * 
     * @see ch.uzh.ifi.ce.cca.domain.Values#contains(java.lang.String)
     */

    public boolean contains(String id) {
        Bidder searchBidder = new Bidder(id);
        return getValueMap().containsKey(searchBidder);
    }

    /*
     * (non-Javadoc)
     * 
     * @see ch.uzh.ifi.ce.cca.domain.Values#toBids()
     */

    public Bids toBids() {
        return new Bids(new HashMap<>(Maps.transformValues(getValueMap(), Strategy.TRUTHFUL::apply)));
    }

}
