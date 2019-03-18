package ch.uzh.ifi.ce.domain;

import java.io.Serializable;

/**
 * Class representing a Good that is sold in a Combinatorial Auction The Good
 * object contains the bids on this Good and bids can be added and removed
 * Comparing and hashing based on id
 * 
 * @author Benedikt B�nz
 * 
 */
public final class Good implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 6681285736188564800L;
    private final boolean dummyGood;
    private final String id;

    public Good(boolean dummyGood, int id) {
        this.dummyGood = dummyGood;
        this.id = String.valueOf(id);
    }

    public Good(boolean dummyGood, String id) {
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
