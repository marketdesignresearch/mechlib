package org.marketdesignresearch.mechlib.domain;

import com.google.common.base.Preconditions;
import lombok.Value;

@Value
public class BundleEntry {
    private Good good;
    private int amount;

    public BundleEntry merge(BundleEntry other) {
        Preconditions.checkArgument(getGood().equals(other.getGood()));
        return new BundleEntry(good, getAmount() + other.getAmount());
    }
}
