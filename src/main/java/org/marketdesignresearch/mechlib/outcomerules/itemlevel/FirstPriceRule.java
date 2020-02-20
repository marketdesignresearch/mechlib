package org.marketdesignresearch.mechlib.outcomerules.itemlevel;

import org.marketdesignresearch.mechlib.core.BidderPayment;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.SingleItemBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.SingleItemBids;

import java.util.Iterator;

public class FirstPriceRule extends SingleItemOutcomeRule {

    public FirstPriceRule(SingleItemBids bids) {
        super(bids);
    }

    public FirstPriceRule(BundleValueBids bids) {
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
