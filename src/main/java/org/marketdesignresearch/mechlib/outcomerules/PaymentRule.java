package org.marketdesignresearch.mechlib.outcomerules;

import org.marketdesignresearch.mechlib.core.Payment;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentationable;

public interface PaymentRule extends MipInstrumentationable {

    Payment getPayment();

}