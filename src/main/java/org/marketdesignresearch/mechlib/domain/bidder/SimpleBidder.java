package org.marketdesignresearch.mechlib.domain.bidder;

import org.marketdesignresearch.mechlib.domain.Bidder;
import org.marketdesignresearch.mechlib.domain.Bundle;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = "value")
public final class SimpleBidder implements Bidder, Serializable {
    private static final long serialVersionUID = -4896848195956099257L;
    
    @Getter
    private final String id;
    @Getter
    private final org.marketdesignresearch.mechlib.domain.bidder.Value value;

    public SimpleBidder(String id) {
        this.id = id;
        value = new Value(ValueType.CATS);
    }

    @Override
    public BigDecimal getValue(Bundle bundle) {
        return value.getXORValueFor(bundle);
    }
}
