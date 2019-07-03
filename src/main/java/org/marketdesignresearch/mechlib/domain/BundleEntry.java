package org.marketdesignresearch.mechlib.domain;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Value;

@Value
public class BundleEntry {
    private Good good;
    private int amount;

    public BundleEntry(Good good, int amount) {
        Preconditions.checkArgument(good.available() >= amount);
        this.good = good;
        this.amount = amount;
    }

    public BundleEntry merge(BundleEntry other) {
        Preconditions.checkArgument(getGood().equals(other.getGood()));
        return new BundleEntry(good, getAmount() + other.getAmount());
    }
}
