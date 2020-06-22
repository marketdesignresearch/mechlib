package org.marketdesignresearch.mechlib.core;

import com.google.common.base.Preconditions;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@ToString
@EqualsAndHashCode
@Value
public class BundleEntry {
    private Good good;
    private int amount;

    public BundleEntry(Good good, int amount) {
        Preconditions.checkArgument(good.getQuantity() >= amount, "Tried to create a bundle entry for more goods than available.");
        Preconditions.checkArgument(amount > 0, "Tried to create a bundle entry with no content.");
        this.good = good;
        this.amount = amount;
    }

    public BundleEntry merge(BundleEntry other) {
        Preconditions.checkArgument(getGood().equals(other.getGood()));
        return new BundleEntry(good, getAmount() + other.getAmount());
    }
}
