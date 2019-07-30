package org.marketdesignresearch.mechlib.outcomerules.itemlevel;

import org.marketdesignresearch.mechlib.core.BidderPayment;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.bid.SingleItemBid;
import org.marketdesignresearch.mechlib.core.bid.SingleItemBids;

import java.util.Iterator;

public class FirstPriceRule extends SingleItemOutcomeRule {

    public FirstPriceRule(SingleItemBids bids) {
        super(bids);
    }

    public FirstPriceRule(Bids bids) {
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
