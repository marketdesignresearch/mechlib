package org.marketdesignresearch.mechlib.domain;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

/**
 * Class representing a simple good that is sold in a Combinatorial Auction
 *
 * Comparing and hashing based on id
 *
 * @author Benedikt Bünz
 *
 */
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString(of = "name")
public final class SimpleGood implements Good {
    private static final long serialVersionUID = 6681285736188564800L;

    @Getter
    private final String name;
    @Getter @EqualsAndHashCode.Exclude
    private final UUID uuid = UUID.randomUUID();
    private final int availability;
    @Getter
    private final boolean dummyGood;

    public SimpleGood(String name) {
        this(name, 1, false);
    }

    public SimpleGood(String name, boolean dummyGood) {
        this(name, 1, dummyGood);
    }

    @Override
    public int available() {
        return availability;
    }
}