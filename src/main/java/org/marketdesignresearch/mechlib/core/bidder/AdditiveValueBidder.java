package org.marketdesignresearch.mechlib.core.bidder;

import com.google.common.base.Preconditions;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.marketdesignresearch.mechlib.core.BundleEntry;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.BundleValue;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.ORValueFunction;

import java.math.RoundingMode;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
public class AdditiveValueBidder extends ORBidder {

    public AdditiveValueBidder(String name, ORValueFunction orValue) {
        super(name, orValue);
        for (BundleValue bundleValue : orValue.getBundleValues()) {
            List<BundleEntry> bundleEntries = bundleValue.getBundle().getBundleEntries();
            Preconditions.checkArgument(bundleEntries.size() == 1);
            Preconditions.checkArgument(bundleEntries.get(0).getAmount() == 1);
        }
        Preconditions.checkArgument(orValue.getBundleValues().stream().map(be -> be.getBundle().getSingleGood()).distinct().count() == orValue.getBundleValues().size());
        StringBuilder sb = new StringBuilder("Bidder with an additive value function with the following values per item (rounded):");
        for (BundleValue bundleValue : getValue().getBundleValues()) {
            sb.append("\n\t- ").append(bundleValue.getBundle()).append(": ").append(bundleValue.getAmount().setScale(2, RoundingMode.HALF_UP));
        }
        setDescription(sb.toString());
        setShortDescription("Additive Value Bidder: " + getName());
    }
}
