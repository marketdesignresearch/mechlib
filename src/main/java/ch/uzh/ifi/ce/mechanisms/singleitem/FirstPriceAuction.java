package ch.uzh.ifi.ce.mechanisms.singleitem;

import ch.uzh.ifi.ce.domain.BidderPayment;
import ch.uzh.ifi.ce.domain.SingleItemAuctionInstance;
import ch.uzh.ifi.ce.domain.singleitem.SingleItemBid;

import java.util.Iterator;

public class FirstPriceAuction extends SingleItemAuction {

    public FirstPriceAuction(SingleItemAuctionInstance auctionInstance) {
        super(auctionInstance);
    }

    @Override
    protected BidderPayment getSingleItemPayment() {
        Iterator<SingleItemBid> iterator = instance.getDescendingHighestBids().iterator();
        if (!iterator.hasNext()) return BidderPayment.ZERO_PAYMENT;
        SingleItemBid highestBid = iterator.next();
        return new BidderPayment(highestBid.getBundleBid().getAmount());
    }
}
