package org.marketdesignresearch.mechlib.mechanisms.itemlevel;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import org.marketdesignresearch.mechlib.domain.*;
import org.marketdesignresearch.mechlib.domain.bid.Bids;
import org.marketdesignresearch.mechlib.domain.bid.SingleItemBid;
import org.marketdesignresearch.mechlib.domain.bid.SingleItemBids;
import org.marketdesignresearch.mechlib.domain.bidder.Bidder;
import org.marketdesignresearch.mechlib.mechanisms.Mechanism;
import org.marketdesignresearch.mechlib.mechanisms.MechanismResult;
import org.marketdesignresearch.mechlib.mechanisms.MetaInfo;
import org.marketdesignresearch.mechlib.mechanisms.itemlevel.tiebreaker.AlphabeticTieBreaker;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class ItemLevelMechanism implements Mechanism {

    protected final Set<SingleItemBids> bidsPerGood = new HashSet<>();

    public ItemLevelMechanism(Bids bids) {
        for (Good good : bids.getGoods()) {
            this.bidsPerGood.add(bids.getBidsPerSingleGood(good));
        }
    }

    @Override
    public final MechanismResult getMechanismResult() {
        MechanismResult result = MechanismResult.NONE;
        for (SingleItemBids bids : bidsPerGood) {
            Iterator<SingleItemBid> iterator = bids.getDescendingHighestBids().iterator();
            if (!iterator.hasNext()) {
                return new MechanismResult(Payment.ZERO, Allocation.EMPTY_ALLOCATION);
            }
            List<SingleItemBid> firstBids = new ArrayList<>();
            firstBids.add(iterator.next());
            while(iterator.hasNext()) {
                SingleItemBid next = iterator.next();
                if (next.getBundleBid().getAmount().equals(firstBids.get(0).getBundleBid().getAmount())) {
                    firstBids.add(next);
                } else {
                    break;
                }
            }
            SingleItemBid firstBid = firstBids.stream()
                    .sorted((a, b) -> new AlphabeticTieBreaker().compare(a, b)).collect(Collectors.toList()).get(0);
            BundleBid winningBid = firstBid.getBundleBid();
            Bidder winner = firstBid.getBidder();
            BidderAllocation bidderAllocation = new BidderAllocation(winningBid.getAmount(), Sets.newHashSet(bids.getItem()), Sets.newHashSet(winningBid));
            Allocation allocation = new Allocation(ImmutableMap.of(winner, bidderAllocation), bids, new MetaInfo());
            Payment payment = new Payment(ImmutableMap.of(winner, getSingleItemPayment(bids)), new MetaInfo());
            result = result.merge(new MechanismResult(payment, allocation));
        }
        return result;
    }

    protected abstract BidderPayment getSingleItemPayment(SingleItemBids bids);
}
