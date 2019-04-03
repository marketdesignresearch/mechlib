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
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString
public final class SimpleGood implements Good, Serializable {
    private static final long serialVersionUID = 6681285736188564800L;

    @Getter
    private final boolean dummyGood;
    @Getter
    private final String id;
    private int availability = 1;

    public SimpleGood(int id) {
        this(false, String.valueOf(id));
    }

    public SimpleGood(String id) {
        this(false, id);
    }

    public SimpleGood(boolean dummyGood, int id) {
        this(dummyGood, String.valueOf(id));
    }

    @Override
    public int available() {
        return availability;
    }
}