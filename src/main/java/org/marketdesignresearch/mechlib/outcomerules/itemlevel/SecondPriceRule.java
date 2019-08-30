package org.marketdesignresearch.mechlib.outcomerules.itemlevel;

import org.marketdesignresearch.mechlib.core.BidderPayment;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.bid.SingleItemBid;
import org.marketdesignresearch.mechlib.core.bid.SingleItemBids;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentation;

import java.util.Iterator;


public class SecondPriceRule extends SingleItemOutcomeRule {

    public SecondPriceRule(SingleItemBids bids) {
        super(bids);
    }

    public SecondPriceRule(Bids bids) {
        super(bids);
    }

    @Override
    protected BidderPayment getSingleItemPayment(SingleItemBids bids) {
        Iterator<SingleItemBid> iterator = bids.getDescendingHighestBids().iterator();
        if (!iterator.hasNext()) return BidderPayment.ZERO_PAYMENT;
        iterator.next();
        if (!iterator.hasNext()) return BidderPayment.ZERO_PAYMENT;
        SingleItemBid secondHighestBid = iterator.next();
        return new BidderPayment(secondHighestBid.getBundleBid().getAmount());
    }

}
