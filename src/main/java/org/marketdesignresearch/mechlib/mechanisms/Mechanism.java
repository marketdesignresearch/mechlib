package org.marketdesignresearch.mechlib.mechanisms;

import org.marketdesignresearch.mechlib.domain.Allocation;
import org.marketdesignresearch.mechlib.domain.Payment;

public interface Mechanism extends PaymentRule, AllocationRule {
    MechanismResult getMechanismResult();

    @Override
    default Payment getPayment() {
        return getMechanismResult().getPayment();
    }


    @Override
    default Allocation getAllocation() {
        return getMechanismResult().getAllocation();
    }
}
