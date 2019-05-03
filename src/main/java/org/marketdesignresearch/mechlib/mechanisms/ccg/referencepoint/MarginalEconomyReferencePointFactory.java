package org.marketdesignresearch.mechlib.mechanisms.ccg.referencepoint;

import com.google.common.base.Objects;
import org.marketdesignresearch.mechlib.domain.*;
import org.marketdesignresearch.mechlib.utils.PrecisionUtils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;

public class MarginalEconomyReferencePointFactory implements ReferencePointFactory {

    @Override
    public Payment computeReferencePoint(Bids bids, Allocation allocation) {
        // TODO: .keySet() assumes availability of 1
        Map<Good, Bidder> winnerMap = new HashMap<>();
        for (Bidder bidder : allocation.getWinners()) {
            allocation.allocationOf(bidder).getBundle().keySet().forEach(g -> winnerMap.put(g, bidder));
        }
        Map<Good, BigDecimal> highestLosingBids = new HashMap<>(winnerMap.size());
        for (Bidder bidder : bids.getBidders()) {
            for (BundleBid bundleBid : bids.getBid(bidder).getBundleBids()) {
                BigDecimal bidPerGood = bundleBid.getAmount().divide(BigDecimal.valueOf(bundleBid.getBundle().size()), MathContext.DECIMAL64);
                bundleBid.getBundle().keySet().stream().filter(good -> !Objects.equal(winnerMap.get(good), bidder)).forEach(good -> highestLosingBids.merge(good, bidPerGood, PrecisionUtils::max));
            }
        }
        Map<Bidder, BidderPayment> paymentMap = new HashMap<>(allocation.getWinners().size());
        for (Bidder winner : allocation.getWinners()) {
            BidderAllocation bidderAllocation = allocation.allocationOf(winner);
            BigDecimal referencePayment = bidderAllocation.getBundle().keySet().stream().map(g -> highestLosingBids.getOrDefault(g, BigDecimal.ZERO))
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
