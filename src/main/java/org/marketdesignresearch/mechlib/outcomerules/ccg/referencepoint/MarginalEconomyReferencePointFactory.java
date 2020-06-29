package org.marketdesignresearch.mechlib.outcomerules.ccg.referencepoint;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.BidderAllocation;
import org.marketdesignresearch.mechlib.core.BidderPayment;
import org.marketdesignresearch.mechlib.core.BundleEntry;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.Payment;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValuePair;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.utils.PrecisionUtils;

import com.google.common.base.Objects;

public class MarginalEconomyReferencePointFactory implements ReferencePointFactory {

    @Override
    public Payment computeReferencePoint(BundleValueBids<?> bids, Allocation allocation) {
        // TODO: assumes availability of 1
        Map<Good, Bidder> winnerMap = new LinkedHashMap<>();
        for (Bidder bidder : allocation.getWinners()) {
            allocation.allocationOf(bidder).getBundle().getBundleEntries().stream().map(BundleEntry::getGood).collect(Collectors.toCollection(LinkedHashSet::new)).forEach(g -> winnerMap.put(g, bidder));
        }
        Map<Good, BigDecimal> highestLosingBids = new LinkedHashMap<>(winnerMap.size());
        for (Bidder bidder : bids.getBidders()) {
            for (BundleExactValuePair bundleBid : bids.getBid(bidder).getBundleBids()) {
                BigDecimal bidPerGood = bundleBid.getAmount().divide(BigDecimal.valueOf(bundleBid.getBundle().getBundleEntries().size()), MathContext.DECIMAL64);
                // TODO: assumes availability of 1
                bundleBid.getBundle().getBundleEntries().stream().map(BundleEntry::getGood).filter(good -> !Objects.equal(winnerMap.get(good), bidder)).forEach(good -> highestLosingBids.merge(good, bidPerGood, PrecisionUtils::max));
            }
        }
        Map<Bidder, BidderPayment> paymentMap = new LinkedHashMap<>(allocation.getWinners().size());
        for (Bidder winner : allocation.getWinners()) {
            BidderAllocation bidderAllocation = allocation.allocationOf(winner);
            BigDecimal referencePayment = bidderAllocation.getBundle().getBundleEntries().stream().map(g -> highestLosingBids.getOrDefault(g.getGood(), BigDecimal.ZERO))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            paymentMap.put(winner, new BidderPayment(referencePayment));
        }
        return new Payment(paymentMap, allocation.getMetaInfo());
    }

    @Override
    public String getName() {
        return "MarginalEconomy";
    }

    @Override
    public boolean belowCore() {
        return false;
    }
}
