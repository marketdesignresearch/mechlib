package org.marketdesignresearch.mechlib.core.bid.bundle;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.BundleEntry;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.bid.Bid;
import org.springframework.data.annotation.PersistenceConstructor;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString @EqualsAndHashCode
public class BundleValueBid<T extends BundleValuePair> implements Bid{
    public static BundleValueBid<BundleValuePair> singleBundleBid(BundleValuePair bundleBid) {
        return new BundleValueBid<BundleValuePair>(ImmutableSet.of(bundleBid));
    }

    @Getter
    private final Set<T> bundleBids;
    
    public BundleValueBid() {
        this(new LinkedHashSet<>());
    }
    
    @PersistenceConstructor
    public BundleValueBid(Set<T> bundleBids) {
    	// not more than one bid per bundle
    	Preconditions.checkArgument(bundleBids.size() == bundleBids.stream().map(b -> b.getBundle()).collect(Collectors.toSet()).size());
    	this.bundleBids = bundleBids;
    }

    public void addBundleBid(T bundleBid) {
    	this.bundleBids.remove(this.getBidForBundle(bundleBid.getBundle()));
        this.bundleBids.add(bundleBid);
    }
    
    public T getBidForBundle(Bundle bundle) {
    	return this.bundleBids.stream().filter(b -> b.getBundle().equals(bundle)).findAny().orElse(null);
    }

    @SuppressWarnings("unchecked")
    public BundleValueBid<T> reducedBy(BigDecimal payoff) {
		LinkedHashSet<T> newBids = (LinkedHashSet<T>) getBundleBids().stream().map(bid -> bid.reducedBy(payoff)).collect(Collectors.toCollection(LinkedHashSet::new));
        return new BundleValueBid<T>(newBids);
    }

    public Set<Good> getGoods() {
        Set<Good> goods = new HashSet<>();
        for (BundleValuePair bundleBid : bundleBids) {
            goods.addAll(bundleBid.getBundle().getBundleEntries().stream().map(BundleEntry::getGood).collect(Collectors.toSet()));
        }
        return goods;
    }
    
    @SuppressWarnings("unchecked")
	public BundleValueBid<T> join(BundleValueBid<T> other) {
    	BundleValueBid<T> result = new BundleValueBid<>();
        getBundleBids().forEach(result::addBundleBid);
        for(T otherBid : other.getBundleBids()) {
        	if(this.getBidForBundle(otherBid.getBundle()) != null) {
        		otherBid = (T)otherBid.joinWith(this.getBidForBundle(otherBid.getBundle()));
        	}
        	result.addBundleBid(otherBid);
        }
        return result;
    }

	@Override
	public boolean isEmpty() {
		return this.getBundleBids().isEmpty();
	}
}

