package org.marketdesignresearch.mechlib.auction.pvm.ml;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import org.marketdesignresearch.mechlib.domain.Bundle;
import org.marketdesignresearch.mechlib.domain.Good;
import org.marketdesignresearch.mechlib.domain.bid.Bid;
import org.marketdesignresearch.mechlib.domain.bidder.Bidder;
import org.marketdesignresearch.mechlib.domain.bidder.value.BundleValue;
import org.marketdesignresearch.mechlib.domain.bidder.value.XORValue;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * This dummy algorithm adds an over-valued value to the currently known reports.
 * This only serves to keep PVM from being "wrong" about the bidder's value function, which leads
 * to new queries.
 */
@RequiredArgsConstructor
public class DummyMLAlgorithm implements MLAlgorithm {
    private final Bidder bidder;
    private final List<? extends Good> goods;
    private Bid bid = new Bid();

    public void addReport(Bid report) {
        bid = bid.join(report);
    }

    public XORValue inferValueFunction() {
        Bundle additionalBundle = getNewBundle();
        XORValue value = new XORValue();
        if (additionalBundle != null) {
            value.addBundleValue(new BundleValue(bidder.getValue(additionalBundle).add(BigDecimal.ONE).multiply(BigDecimal.TEN), additionalBundle));
        }
        bid.getBundleBids().forEach(bb -> value.addBundleValue(new BundleValue(bb.getAmount(), bb.getBundle())));
        return value;
    }

    private Bundle getNewBundle() {
        goods.forEach(good -> Preconditions.checkState(good.available() == 1, "Can't use dummy algorithm with multi-availability-goods."));
        Set<Good> goodSet = Sets.newHashSet(goods);
        Set<Set<Good>> powerSet = Sets.powerSet(goodSet);
        for (Set<Good> combination : powerSet) {
            Bundle bundle = Bundle.singleGoods(combination);
            if (bid.getBundleBids().stream().noneMatch(bb -> bb.getBundle().equals(bundle))) {
                return bundle;
            }
        }
        return null;
    }
}
