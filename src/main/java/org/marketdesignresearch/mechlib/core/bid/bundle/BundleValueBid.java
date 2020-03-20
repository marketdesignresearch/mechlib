package org.marketdesignresearch.mechlib.core.bid.bundle;

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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString @EqualsAndHashCode
public abstract class BundleValueBid<T extends BundleExactValuePair> implements Bid{

    @Getter
    private final Set<T> bundleBids;
    
    public BundleValueBid() {
        this(new LinkedHashSet<>());
    }
    
    @PersistenceConstructor
    public BundleValueBid(Set<T> bundleBids) {
    	// not more than one bid per bundle
    	Preconditions.checkArgument(bundleBids.size() == bundleBids.stream().map(BundleExactValuePair::getBundle).collect(Collectors.toSet()).size());
    	this.bundleBids = bundleBids;
    }

    public void addBundleBid(T bundleBid) {
    	this.bundleBids.remove(this.getBidForBundle(bundleBid.getBundle()));
        this.bundleBids.add(bundleBid);
    }
    
    public T getBidForBundle(Bundle bundle) {
    	return this.bundleBids.stream().filter(b -> b.getBundle().equals(bundle)).findAny().orElse(null);
    }

    public Set<Good> getGoods() {
        Set<Good> goods = new HashSet<>();
        for (BundleExactValuePair bundleBid : bundleBids) {
            goods.addAll(bundleBid.getBundle().getBundleEntries().stream().map(BundleEntry::getGood).collect(Collectors.toSet()));
        }
        return goods;
    }
    
    public abstract BundleValueBid<T> join(BundleValueBid<T> other);

	@Override
	public boolean isEmpty() {
		return this.getBundleBids().isEmpty();
	}
}

