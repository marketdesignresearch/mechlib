package ch.uzh.ifi.ce.domain;

import lombok.*;

import java.io.Serializable;

/**
 * Class representing a simple good that is sold in a Combinatorial Auction
 *
 * Comparing and hashing based on id
 *
 * @author Benedikt Bünz
 *
 */
@RequiredArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
public final class SimpleGood implements Good, Serializable {
    private static final long serialVersionUID = 6681285736188564800L;

    @Getter
    private final String id;
    private final int availability;
    @Getter
    private final boolean dummyGood;

    public SimpleGood(String id) {
        this(id, 1, false);
    }

    public SimpleGood(String id, boolean dummyGood) {
        this(id, 1, dummyGood);
    }

    @Override
    public int available() {
        return availability;
    }
}