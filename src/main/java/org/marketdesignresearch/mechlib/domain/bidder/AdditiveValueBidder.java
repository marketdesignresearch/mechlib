package org.marketdesignresearch.mechlib.domain.bidder;

import com.google.common.base.Preconditions;
import lombok.EqualsAndHashCode;
import org.marketdesignresearch.mechlib.domain.BundleEntry;
import org.marketdesignresearch.mechlib.domain.bidder.value.BundleValue;
import org.marketdesignresearch.mechlib.domain.bidder.value.ORValue;

import java.math.RoundingMode;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
public class AdditiveValueBidder extends ORBidder {

    private final String description;

    // TODO: Provide constructor for easier creation of this bidder
    public AdditiveValueBidder(String name, ORValue orValue) {
        super(name, orValue);
        for (BundleValue bundleValue : orValue.getBundleValues()) {
            List<BundleEntry> bundleEntries = bundleValue.getBundle().getBundleEntries();
            Preconditions.checkArgument(bundleEntries.size() == 1);
            Preconditions.checkArgument(bundleEntries.get(0).getAmount() == 1);
        }
        StringBuilder sb = new StringBuilder("Bidder with an additive value function with the following values per item (rounded):");
        for (BundleValue bundleValue : getValue().getBundleValues()) {
            sb.append("\n\t- ").append(bundleValue.getBundle()).append(": ").append(bundleValue.getAmount().setScale(2, RoundingMode.HALF_UP));
        }
        this.description = sb.toString();
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getShortDescription() {
        return "Additive Value Bidder: " + getName();
    }
}
