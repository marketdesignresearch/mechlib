package ch.uzh.ifi.ce.mechanisms.ccg.referencepoint;

import ch.uzh.ifi.ce.domain.*;
import ch.uzh.ifi.ce.mechanisms.MetaInfo;
import ch.uzh.ifi.ce.winnerdetermination.XORWinnerDetermination;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.apache.commons.math3.util.CombinatoricsUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ShapleyReferencePointFactory implements ReferencePointFactory {

    @Override
    public Payment computeReferencePoint(AuctionInstance auctionInstance, Allocation allocation) {
        Map<Bidder, BidderPayment> referencePointMap = new HashMap<>(allocation.getWinners().size());
        for (Bidder bidder : allocation.getWinners()) {
            Set<Bidder> biddersWithout = new HashSet<>(auctionInstance.getBidders());
            biddersWithout.remove(bidder);
            double shapleyValue = 0;
            int n = auctionInstance.getBidders().size() + 1;
            Set<Bidder> singeltonSet= ImmutableSet.of(bidder);
            for (Set<Bidder> biddersWithoutSubset : Sets.powerSet(biddersWithout)) {
                AuctionInstance s = auctionInstance.of(Sets.union(singeltonSet,biddersWithoutSubset));

                Allocation subsetAllocation = new XORWinnerDetermination(s).getAllocation();
                if (subsetAllocation.getWinners().contains(bidder)) {
                    AuctionInstance auctionWithoutSAndBidder = s.without(bidder);
                    Allocation withoutBidder = new XORWinnerDetermination(auctionWithoutSAndBidder).getAllocation();
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
