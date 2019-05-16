package org.marketdesignresearch.mechlib.domain;

import lombok.Value;

@Value
public class BundleEntry {
    private Good good;
    private int amount;
}
