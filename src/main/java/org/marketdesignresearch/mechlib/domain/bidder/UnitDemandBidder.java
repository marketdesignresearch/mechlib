package org.marketdesignresearch.mechlib.domain.bidder;

import com.google.common.base.Preconditions;
import lombok.EqualsAndHashCode;
import org.marketdesignresearch.mechlib.domain.Bundle;
import org.marketdesignresearch.mechlib.domain.bidder.value.BundleValue;
import org.marketdesignresearch.mechlib.domain.bidder.value.XORValue;

import java.math.BigDecimal;
import java.math.RoundingMode;

@EqualsAndHashCode(callSuper = true)
public class UnitDemandBidder extends XORBidder {

    private final String description;

    // TODO: Provide constructor for easier creation of this bidder (e.g. by only providing the one value and set of goods)
    public UnitDemandBidder(String name, XORValue xorValue) {
        super(name, xorValue);
        BigDecimal value = null;
        for (BundleValue bundleValue : xorValue.getBundleValues()) {
            if (Bundle.EMPTY.equals(bundleValue.getBundle())) {
                Preconditions.checkArgument(bundleValue.getAmount().equals(BigDecimal.ZERO));
            } else {
                if (value == null) {
                    value = bundleValue.getAmount();
                } else {
                    Preconditions.checkArgument(bundleValue.getAmount().equals(value));
                }
            }
        }
        Preconditions.checkNotNull(value);
        this.description = "This bidder has unit demand: Zero value if nothing is won, or "
                + value.setScale(2, RoundingMode.HALF_UP) + " (rounded) if any good is won.";
    }

    @Override
    public String getDescription() {
        return description;
    }
}
