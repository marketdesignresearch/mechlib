package ch.uzh.ifi.ce.domain;

import com.google.common.collect.ImmutableSet;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Bid {
    private final Set<BundleBid> bundleBids;

    public Bid(Set<BundleBid> bundleBids) {
        this.bundleBids = bundleBids;
    }

    public Bid() {
        this(new LinkedHashSet<>());
    }

    public boolean addBundleBid(BundleBid bundleBid) {
        return bundleBids.add(bundleBid);
    }

    public Set<BundleBid> getBundleBids() {
        return bundleBids;
    }

    public Bid reducedBy(BigDecimal payoff) {
        LinkedHashSet<BundleBid> newBids = getBundleBids().stream().map(bid -> bid.reducedBy(payoff)).collect(Collectors.toCollection(() -> new LinkedHashSet<>()));
        return new Bid(newBids);
    }

    @Override
    public String toString() {
        return "Bid[bundleBids=" + bundleBids + "]";
    }

    public static Bid singleBundleBid(BundleBid bundleBid) {
        return new Bid(ImmutableSet.of(bundleBid));
    }
}
