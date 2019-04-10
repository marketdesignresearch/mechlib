package ch.uzh.ifi.ce.mechanisms.singleitem;

import ch.uzh.ifi.ce.domain.*;
import ch.uzh.ifi.ce.domain.singleitem.SingleItemAuctionInstance;
import ch.uzh.ifi.ce.domain.singleitem.SingleItemBid;

import java.util.Iterator;

public class SecondPriceAuction extends SingleItemAuction {

    public SecondPriceAuction(SingleItemAuctionInstance auctionInstance) {
        super(auctionInstance);
    }

    @Override
    protected BidderPayment getSingleItemPayment() {
        Iterator<SingleItemBid> iterator = instance.getDescendingHighestBids().iterator();
        if (!iterator.hasNext()) return BidderPayment.ZERO_PAYMENT;
        iterator.next();
        if (!iterator.hasNext()) return BidderPayment.ZERO_PAYMENT;
        SingleItemBid secondHighestBid = iterator.next();
        return new BidderPayment(secondHighestBid.getBundleBid().getAmount());
    }
}
