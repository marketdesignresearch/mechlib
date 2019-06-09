package org.marketdesignresearch.mechlib.domain.bidder.value;

import org.marketdesignresearch.mechlib.domain.bid.Bid;
import org.marketdesignresearch.mechlib.domain.Bundle;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.function.UnaryOperator;

public interface Value extends Serializable {

    BigDecimal getValueFor(Bundle bundle);

    Bid toBid(UnaryOperator<BigDecimal> bundleBidOperator);

    default Bid toBid() {
        return toBid(UnaryOperator.identity());
    }
}
