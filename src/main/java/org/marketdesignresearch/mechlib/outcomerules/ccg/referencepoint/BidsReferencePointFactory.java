package org.marketdesignresearch.mechlib.outcomerules.ccg.referencepoint;

import java.util.Map;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.BidderPayment;
import org.marketdesignresearch.mechlib.core.Payment;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.metainfo.MetaInfo;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class BidsReferencePointFactory implements ReferencePointFactory {

	@Override
	public Payment computeReferencePoint(BundleValueBids<?> bids, Allocation allocation) {
		Map<Bidder, BidderPayment> paymentMap = ImmutableMap
				.copyOf(Maps.transformValues(allocation.getTradesMap(), ba -> new BidderPayment(ba.getValue())));
		return new Payment(paymentMap, new MetaInfo());
	}

	@Override
	public String getName() {
		return "BIDS";
	}

	@Override
	public boolean belowCore() {
		return false;
	}

}
