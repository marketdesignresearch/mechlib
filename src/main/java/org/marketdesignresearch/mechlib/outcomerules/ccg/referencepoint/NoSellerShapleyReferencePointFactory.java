package org.marketdesignresearch.mechlib.outcomerules.ccg.referencepoint;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.apache.commons.math3.util.CombinatoricsUtils;
import org.marketdesignresearch.mechlib.core.*;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.metainfo.MetaInfo;
import org.marketdesignresearch.mechlib.winnerdetermination.XORWinnerDetermination;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NoSellerShapleyReferencePointFactory implements ReferencePointFactory {

    @Override
    public Payment computeReferencePoint(Bids bids, Allocation allocation) {
        Map<Bidder, BidderPayment> referencePointMap = new HashMap<>(allocation.getWinners().size());
        for (Bidder bidder : allocation.getWinners()) {
            Set<Bidder> biddersWithout = new HashSet<>(bids.getBidders());
            biddersWithout.remove(bidder);
            double shapleyValue = 0;
            int n = bids.getBidders().size() ;
            Set<Bidder> singeltonSet= ImmutableSet.of(bidder);
            for (Set<Bidder> biddersWithoutSubset : Sets.powerSet(biddersWithout)) {
                Bids s = bids.of(Sets.union(singeltonSet,biddersWithoutSubset));

                Allocation subsetAllocation = new XORWinnerDetermination(s).getAllocation();
                if (subsetAllocation.getWinners().contains(bidder)) {
                    Bids bidsWithoutSAndBidder = s.without(bidder);
                    Allocation withoutBidder = new XORWinnerDetermination(bidsWithoutSAndBidder).getAllocation();
                    double allocationDiff = subsetAllocation.getTotalAllocationValue().subtract(withoutBidder.getTotalAllocationValue()).doubleValue();
                    int sizeS = s.getBidders().size()-1;
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
