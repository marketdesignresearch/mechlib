package org.marketdesignresearch.mechlib.mechanisms;

import org.marketdesignresearch.mechlib.domain.Allocation;
import org.marketdesignresearch.mechlib.domain.Payment;

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
