package org.marketdesignresearch.mechlib.mechanisms.singleitem;

import org.marketdesignresearch.mechlib.domain.BidderPayment;
import org.marketdesignresearch.mechlib.domain.singleitem.SingleItemAuctionInstance;
import org.marketdesignresearch.mechlib.domain.singleitem.SingleItemBid;

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
