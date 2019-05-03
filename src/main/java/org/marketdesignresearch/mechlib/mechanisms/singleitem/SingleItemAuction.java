package org.marketdesignresearch.mechlib.mechanisms.singleitem;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import org.marketdesignresearch.mechlib.domain.*;
import org.marketdesignresearch.mechlib.domain.singleitem.SingleItemAuctionInstance;
import org.marketdesignresearch.mechlib.domain.singleitem.SingleItemBid;
import org.marketdesignresearch.mechlib.mechanisms.AuctionMechanism;
import org.marketdesignresearch.mechlib.mechanisms.AuctionResult;
import org.marketdesignresearch.mechlib.mechanisms.MetaInfo;

import java.util.Iterator;

public abstract class SingleItemAuction implements AuctionMechanism {

    protected SingleItemAuctionInstance instance;

    public SingleItemAuction(SingleItemAuctionInstance auctionInstance) {
        this.instance = auctionInstance;
    }

    @Override
    public final AuctionResult getAuctionResult() {
        Iterator<SingleItemBid> iterator = instance.getDescendingHighestBids().iterator();
        if (!iterator.hasNext()) {
            return new AuctionResult(Payment.ZERO, Allocation.EMPTY_ALLOCATION);
        }
        SingleItemBid firstBid = iterator.next();
        BundleBid winningBid = firstBid.getBundleBid();
        Bidder winner = firstBid.getBidder();
        BidderAllocation bidderAllocation = new BidderAllocation(winningBid.getAmount(), Sets.newHashSet(instance.getItem()), Sets.newHashSet(winningBid));
        Allocation allocation = new Allocation(ImmutableMap.of(winner, bidderAllocation), instance.getBids(), new MetaInfo());
        Payment payment = new Payment(ImmutableMap.of(winner, getSingleItemPayment()), new MetaInfo());
        return new AuctionResult(payment, allocation);
    }

    protected abstract BidderPayment getSingleItemPayment();
}
