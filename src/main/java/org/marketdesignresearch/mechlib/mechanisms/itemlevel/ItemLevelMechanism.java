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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
            SingleItemBid firstBid = iterator.next();
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
