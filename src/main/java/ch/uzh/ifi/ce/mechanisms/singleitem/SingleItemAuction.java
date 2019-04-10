package ch.uzh.ifi.ce.mechanisms.singleitem;

import ch.uzh.ifi.ce.domain.*;
import ch.uzh.ifi.ce.domain.singleitem.SingleItemAuctionInstance;
import ch.uzh.ifi.ce.domain.singleitem.SingleItemBid;
import ch.uzh.ifi.ce.mechanisms.AuctionMechanism;
import ch.uzh.ifi.ce.mechanisms.AuctionResult;
import ch.uzh.ifi.ce.mechanisms.MetaInfo;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

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
