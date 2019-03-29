package ch.uzh.ifi.ce.domain;

import java.io.Serializable;

public class Bidder implements Serializable {

    private static final long serialVersionUID = -4896848195956099257L;
    private final String id;

    public Bidder(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Bidder_" + id;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object.getClass() != this.getClass()) return false;
        Bidder otherBidder = (Bidder) object;
        return id.equals(otherBidder.getId());
    }

    /**
     * based on id
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }

}
