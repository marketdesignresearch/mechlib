package org.marketdesignresearch.mechlib.mechanisms.singleitem;

import org.marketdesignresearch.mechlib.domain.BidderPayment;
import org.marketdesignresearch.mechlib.domain.bid.Bids;
import org.marketdesignresearch.mechlib.domain.bid.SingleItemBid;
import org.marketdesignresearch.mechlib.domain.bid.SingleItemBids;

import java.util.Iterator;

public class FirstPriceAuction extends SingleItemAuction {

    public FirstPriceAuction(SingleItemBids bids) {
        super(bids);
    }

    public FirstPriceAuction(Bids bids) {
        super(bids);
    }

    @Override
    protected BidderPayment getSingleItemPayment() {
        Iterator<SingleItemBid> iterator = bids.getDescendingHighestBids().iterator();
        if (!iterator.hasNext()) return BidderPayment.ZERO_PAYMENT;
        SingleItemBid highestBid = iterator.next();
        return new BidderPayment(highestBid.getBundleBid().getAmount());
    }
}
