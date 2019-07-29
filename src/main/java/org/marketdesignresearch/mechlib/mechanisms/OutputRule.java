package org.marketdesignresearch.mechlib.mechanisms;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.Payment;

public interface OutputRule extends PaymentRule, AllocationRule {
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
