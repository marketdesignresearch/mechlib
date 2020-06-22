package org.marketdesignresearch.mechlib.outcomerules.ccg.referencepoint;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.util.CombinatoricsUtils;
import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.BidderPayment;
import org.marketdesignresearch.mechlib.core.Payment;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.metainfo.MetaInfo;
import org.marketdesignresearch.mechlib.winnerdetermination.XORWinnerDetermination;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class ShapleyReferencePointFactory implements ReferencePointFactory {

    @Override
    public Payment computeReferencePoint(BundleValueBids<?> bids, Allocation allocation) {
        Map<Bidder, BidderPayment> referencePointMap = new HashMap<>(allocation.getWinners().size());
        for (Bidder bidder : allocation.getWinners()) {
            Set<Bidder> biddersWithout = new HashSet<>(bids.getBidders());
            biddersWithout.remove(bidder);
            double shapleyValue = 0;
            int n = bids.getBidders().size() + 1;
            Set<Bidder> singeltonSet= ImmutableSet.of(bidder);
            for (Set<Bidder> biddersWithoutSubset : Sets.powerSet(biddersWithout)) {
                BundleValueBids<?> s = bids.of(Sets.union(singeltonSet,biddersWithoutSubset));

                Allocation subsetAllocation = new XORWinnerDetermination(s).getAllocation();
                if (subsetAllocation.getWinners().contains(bidder)) {
                    BundleValueBids<?> bidsWithoutSAndBidder = s.without(bidder);
                    Allocation withoutBidder = new XORWinnerDetermination(bidsWithoutSAndBidder).getAllocation();
                    double allocationDiff = subsetAllocation.getTotalAllocationValue().subtract(withoutBidder.getTotalAllocationValue()).doubleValue();
                    int sizeS = s.getBidders().size();
                    long numerator = CombinatoricsUtils.factorial(sizeS) * CombinatoricsUtils.factorial(n - sizeS - 1);
                    long denominator = CombinatoricsUtils.factorial(n);
                    shapleyValue += allocationDiff * numerator / denominator;
                }
            }
            referencePointMap.put(bidder, new BidderPayment(BigDecimal.valueOf(shapleyValue)));

        }
        return new Payment(referencePointMap, new MetaInfo());
    }

    @Override
    public String getName() {
        return "Shapley";
    }

    @Override
    public boolean belowCore() {
        return false;
    }

}
