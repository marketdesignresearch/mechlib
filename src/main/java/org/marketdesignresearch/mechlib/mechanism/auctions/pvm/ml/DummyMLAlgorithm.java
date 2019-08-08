package org.marketdesignresearch.mechlib.mechanism.auctions.pvm.ml;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.BundleEntry;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.bid.Bid;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.BundleValue;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.XORValueFunction;
import org.springframework.data.annotation.PersistenceConstructor;

import java.math.BigDecimal;
import java.util.*;

/**
 * This dummy algorithm adds an over-valued value to the currently known reports.
 * This only serves to keep PVM from being "wrong" about the bidder's value function, which leads
 * to new queries.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@PersistenceConstructor}))
@RequiredArgsConstructor
public class DummyMLAlgorithm implements MLAlgorithm {
    private final Bidder bidder;
    private final List<? extends Good> goods;
    private Bid bid = new Bid();

    public void addReport(Bid report) {
        bid = bid.join(report);
    }

    public XORValueFunction inferValueFunction() {
        Bundle additionalBundle = getNewBundle();
        Set<BundleValue> value = new HashSet<>();
        if (additionalBundle != null) {
            value.add(new BundleValue(bidder.getValue(additionalBundle).add(BigDecimal.valueOf(10000000)), additionalBundle));
        }
        bid.getBundleBids().forEach(bb -> value.add(new BundleValue(bb.getAmount(), bb.getBundle())));
        return new XORValueFunction(ImmutableSet.copyOf(value));
    }

    private Bundle getNewBundle() {
        if (goods.size() < 30 && goods.stream().allMatch(good -> (good.getQuantity() == 1))) {
            Set<Good> goodSet = Sets.newHashSet(goods);
            Set<Set<Good>> powerSet = Sets.powerSet(goodSet);
            for (Set<Good> combination : powerSet) {
                Bundle bundle = Bundle.of(combination);
                if (bid.getBundleBids().stream().noneMatch(bb -> bb.getBundle().equals(bundle))) {
                    return bundle;
                }
            }
        } else {
            Iterator<? extends Good> iterator = goods.iterator();
            Random random = new Random();
            Set<BundleEntry> bundleEntries = new HashSet<>();
            while (iterator.hasNext()) {
                Good good = iterator.next();
                if (random.nextBoolean()) {
                    bundleEntries.add(new BundleEntry(good, random.nextInt(good.getQuantity()) + 1));
                }
            }
            Bundle bundle = new Bundle(bundleEntries);
            if (bid.getBundleBids().stream().noneMatch(bb -> bb.getBundle().equals(bundle))) {
                return bundle;
            }
        }
        return null;
    }
}
