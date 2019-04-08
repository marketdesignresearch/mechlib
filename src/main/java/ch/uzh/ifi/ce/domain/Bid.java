package ch.uzh.ifi.ce.domain;

import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@ToString
public class Bid {
    @Getter
    private final Set<BundleBid> bundleBids;

    public Bid() {
        this(new LinkedHashSet<>());
    }

    public boolean addBundleBid(BundleBid bundleBid) {
        return bundleBids.add(bundleBid);
    }

    public BundleBid getBundleBid(Map<Good, Integer> bundleBid) {
        return bundleBids.stream().filter(b -> b.getBundle().equals(bundleBid)).findFirst().orElse(null);
}

    public Bid reducedBy(BigDecimal payoff) {
        LinkedHashSet<BundleBid> newBids = getBundleBids().stream().map(bid -> bid.reducedBy(payoff)).collect(Collectors.toCollection(LinkedHashSet::new));
        return new Bid(newBids);
    }

    public static Bid singleBundleBid(BundleBid bundleBid) {
        return new Bid(ImmutableSet.of(bundleBid));
    }

    public Bid join(Bid other) {
        Bid result = new Bid();
        getBundleBids().forEach(result::addBundleBid);
        other.getBundleBids().forEach(result::addBundleBid);
        return result;
    }
}

