package org.marketdesignresearch.mechlib.outcomerules.itemlevel;

import java.util.Iterator;

import org.marketdesignresearch.mechlib.core.BidderPayment;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.SingleItemBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.SingleItemBids;

public class SecondPriceRule extends SingleItemOutcomeRule {

	public SecondPriceRule(SingleItemBids bids) {
		super(bids);
	}

	public SecondPriceRule(BundleValueBids<?> bids) {
		super(bids);
	}

	@Override
	protected BidderPayment getSingleItemPayment(SingleItemBids bids) {
		Iterator<SingleItemBid> iterator = bids.getDescendingHighestBids().iterator();
		if (!iterator.hasNext())
			return BidderPayment.ZERO_PAYMENT;
		iterator.next();
		if (!iterator.hasNext())
			return BidderPayment.ZERO_PAYMENT;
		SingleItemBid secondHighestBid = iterator.next();
		return new BidderPayment(secondHighestBid.getBundleBid().getAmount());
	}

}
