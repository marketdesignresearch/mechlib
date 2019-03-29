package ch.uzh.ifi.ce.mechanisms.ccg.referencepoint;

import ch.uzh.ifi.ce.domain.*;
import ch.uzh.ifi.ce.mechanisms.MetaInfo;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.Map;

public class BidsReferencePointFactory implements ReferencePointFactory {

    @Override
    public Payment computeReferencePoint(AuctionInstance auctionInstance, Allocation allocation) {
        Map<Bidder, BidderPayment> paymentMap = ImmutableMap.copyOf(Maps.transformValues(allocation.getTradesMap(), ba -> new BidderPayment(ba.getValue())));
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
