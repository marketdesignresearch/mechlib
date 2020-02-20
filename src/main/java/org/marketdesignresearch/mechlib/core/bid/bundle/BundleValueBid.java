package org.marketdesignresearch.mechlib.core.bid.bundle;

import com.google.common.collect.ImmutableSet;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import org.marketdesignresearch.mechlib.core.BundleEntry;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.bid.Bid;
import org.springframework.data.annotation.PersistenceConstructor;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor(onConstructor = @__({@PersistenceConstructor}))
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

    public boolean addBundleBid(T bundleBid) {
        return bundleBids.add(bundleBid);
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

    public BundleValueBid<T> joinTypesafe(BundleValueBid<T> other) {
        BundleValueBid<T> result = new BundleValueBid<>();
        getBundleBids().forEach(result::addBundleBid);
        other.getBundleBids().forEach(result::addBundleBid);
        return result;
    }
    
    public BundleValueBid<T> join(BundleValueBid<T> other) {
    	BundleValueBid<T> result = new BundleValueBid<>();
        getBundleBids().forEach(result::addBundleBid);
        other.getBundleBids().forEach(result::addBundleBid);
        return result;
    }
}

