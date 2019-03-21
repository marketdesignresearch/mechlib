package ch.uzh.ifi.ce.mechanisms;

import ch.uzh.ifi.ce.domain.Allocation;
import ch.uzh.ifi.ce.domain.AuctionResult;
import ch.uzh.ifi.ce.domain.Payment;

public interface AuctionMechanism extends PaymentMechanism, Allocator {
    AuctionResult getAuctionResult();

    @Override
    default Payment getPayment() {
        return getAuctionResult().getPayment();
    }


    @Override
    default Allocation getAllocation() {
        return getAuctionResult().getAllocation();
    }
}
