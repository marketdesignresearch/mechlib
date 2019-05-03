package org.marketdesignresearch.mechlib.mechanisms.singleitem;

import org.marketdesignresearch.mechlib.domain.BidderPayment;
import org.marketdesignresearch.mechlib.domain.singleitem.SingleItemAuctionInstance;
import org.marketdesignresearch.mechlib.domain.singleitem.SingleItemBid;

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
