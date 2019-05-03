package org.marketdesignresearch.mechlib.mechanisms.singleitem;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import org.marketdesignresearch.mechlib.domain.*;
import org.marketdesignresearch.mechlib.domain.singleitem.SingleItemBid;
import org.marketdesignresearch.mechlib.domain.singleitem.SingleItemBids;
import org.marketdesignresearch.mechlib.mechanisms.AuctionMechanism;
import org.marketdesignresearch.mechlib.mechanisms.AuctionResult;
import org.marketdesignresearch.mechlib.mechanisms.MetaInfo;

import java.util.Iterator;

@RequiredArgsConstructor
public abstract class SingleItemAuction implements AuctionMechanism {

    protected final SingleItemBids bids;

    @Override
    public final AuctionResult getAuctionResult() {
        Iterator<SingleItemBid> iterator = bids.getDescendingHighestBids().iterator();
        if (!iterator.hasNext()) {
            return new AuctionResult(Payment.ZERO, Allocation.EMPTY_ALLOCATION);
        }
        SingleItemBid firstBid = iterator.next();
        BundleBid winningBid = firstBid.getBundleBid();
        Bidder winner = firstBid.getBidder();
        BidderAllocation bidderAllocation = new BidderAllocation(winningBid.getAmount(), Sets.newHashSet(bids.getItem()), Sets.newHashSet(winningBid));
        Allocation allocation = new Allocation(ImmutableMap.of(winner, bidderAllocation), bids, new MetaInfo());
        Payment payment = new Payment(ImmutableMap.of(winner, getSingleItemPayment()), new MetaInfo());
        return new AuctionResult(payment, allocation);
    }

    protected abstract BidderPayment getSingleItemPayment();
}
