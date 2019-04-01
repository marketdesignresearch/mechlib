package ch.uzh.ifi.ce.domain;

import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
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

    public Bid reducedBy(BigDecimal payoff) {
        LinkedHashSet<BundleBid> newBids = getBundleBids().stream().map(bid -> bid.reducedBy(payoff)).collect(Collectors.toCollection(() -> new LinkedHashSet<>()));
        return new Bid(newBids);
    }

    public static Bid singleBundleBid(BundleBid bundleBid) {
        return new Bid(ImmutableSet.of(bundleBid));
    }
}

