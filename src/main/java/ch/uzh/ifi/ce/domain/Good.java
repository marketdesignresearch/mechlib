package ch.uzh.ifi.ce.domain;


/**
 * Class representing a Good that is sold in a Combinatorial Auction
 *
 * @author Benedikt Bünz
 * 
 */
public interface Good {

    default boolean isDummyGood() {
        return false;
    }

    String getId();

    default int available() {
        return 1;
    }

}
