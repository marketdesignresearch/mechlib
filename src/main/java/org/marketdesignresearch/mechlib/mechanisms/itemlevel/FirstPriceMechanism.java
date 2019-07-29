package org.marketdesignresearch.mechlib.mechanisms.itemlevel;

import org.marketdesignresearch.mechlib.core.BidderPayment;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.bid.SingleItemBid;
import org.marketdesignresearch.mechlib.core.bid.SingleItemBids;

import java.util.Iterator;

public class FirstPriceMechanism extends ItemLevelMechanism {

    public FirstPriceMechanism(SingleItemBids bids) {
        super(bids);
    }

    public FirstPriceMechanism(Bids bids) {
        super(bids);
    }

    @Override
    protected BidderPayment getSingleItemPayment(SingleItemBids bids) {
        Iterator<SingleItemBid> iterator = bids.getDescendingHighestBids().iterator();
        if (!iterator.hasNext()) return BidderPayment.ZERO_PAYMENT;
        SingleItemBid highestBid = iterator.next();
        return new BidderPayment(highestBid.getBundleBid().getAmount());
    }
}
