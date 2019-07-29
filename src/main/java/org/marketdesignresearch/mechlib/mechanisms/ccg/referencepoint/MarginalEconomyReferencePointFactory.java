package org.marketdesignresearch.mechlib.mechanisms.ccg.referencepoint;

import com.google.common.base.Objects;
import org.marketdesignresearch.mechlib.core.*;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.utils.PrecisionUtils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class MarginalEconomyReferencePointFactory implements ReferencePointFactory {

    @Override
    public Payment computeReferencePoint(Bids bids, Allocation allocation) {
        // TODO: assumes availability of 1
        Map<Good, Bidder> winnerMap = new HashMap<>();
        for (Bidder bidder : allocation.getWinners()) {
            allocation.allocationOf(bidder).getBundle().getBundleEntries().stream().map(BundleEntry::getGood).collect(Collectors.toSet()).forEach(g -> winnerMap.put(g, bidder));
        }
        Map<Good, BigDecimal> highestLosingBids = new HashMap<>(winnerMap.size());
        for (Bidder bidder : bids.getBidders()) {
            for (BundleBid bundleBid : bids.getBid(bidder).getBundleBids()) {
                BigDecimal bidPerGood = bundleBid.getAmount().divide(BigDecimal.valueOf(bundleBid.getBundle().getBundleEntries().size()), MathContext.DECIMAL64);
                // TODO: assumes availability of 1
                bundleBid.getBundle().getBundleEntries().stream().map(BundleEntry::getGood).filter(good -> !Objects.equal(winnerMap.get(good), bidder)).forEach(good -> highestLosingBids.merge(good, bidPerGood, PrecisionUtils::max));
            }
        }
        Map<Bidder, BidderPayment> paymentMap = new HashMap<>(allocation.getWinners().size());
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
