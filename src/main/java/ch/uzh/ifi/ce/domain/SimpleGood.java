package ch.uzh.ifi.ce.domain;

import java.io.Serializable;

/**
 * Class representing a simple good that is sold in a Combinatorial Auction
 *
 * Comparing and hashing based on id
 *
 * @author Benedikt Bünz
 *
 */
public final class SimpleGood implements Good, Serializable {

    private static final long serialVersionUID = 6681285736188564800L;
    private final boolean dummyGood;
    private final String id;

    public SimpleGood(int id) {
        this(false, id);
    }

    public SimpleGood(String id) {
        this(false, id);
    }

    public SimpleGood(boolean dummyGood, int id) {
        this(dummyGood, String.valueOf(id));
    }

    public SimpleGood(boolean dummyGood, String id) {
        this.dummyGood = dummyGood;
        this.id = id;
    }

    public boolean isDummyGood() {
        return dummyGood;
    }

    public String getId() {
        return id;
    }

    /**
     * based on id
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null)
            return false;
        if (getClass() != object.getClass())
            return false;
        Good otherGood = (Good) object;
        return id.equals(otherGood.getId());
    }

    /**
     * based on id
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return isDummyGood() ? id : id;
    }

}