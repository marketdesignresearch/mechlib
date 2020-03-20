package org.marketdesignresearch.mechlib.core;

import java.util.UUID;

import org.springframework.data.annotation.PersistenceConstructor;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Class representing a simple good that is sold in a Combinatorial Auction
 *
 * Comparing and hashing based on id
 *
 * @author Benedikt Bünz
 *
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@PersistenceConstructor}))
@EqualsAndHashCode
@ToString(of = "name")
public final class SimpleGood implements Good {
    private static final long serialVersionUID = 6681285736188564800L;

    @Getter
    private final String name;
    @Getter @EqualsAndHashCode.Exclude
    private final UUID uuid;
    private final int availability;
    @Getter
    private final boolean dummyGood;

    public SimpleGood(String name) {
        this(name, 1, false);
    }

    public SimpleGood(String name, boolean dummyGood) {
        this(name, 1, dummyGood);
    }

    public SimpleGood(String name, int availability, boolean dummyGood) {
        this(name, UUID.randomUUID(), availability, dummyGood);
    }

    @Override
    public int getQuantity() {
        return availability;
    }
}