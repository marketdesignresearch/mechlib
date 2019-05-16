package org.marketdesignresearch.mechlib.domain;


import java.io.Serializable;

/**
 * Class representing a Good that is sold in a Combinatorial Auction
 *
 * @author Benedikt Bünz
 * 
 */
public interface Good extends Serializable {

    default boolean isDummyGood() {
        return false;
    }

    String getId();

    default int available() {
        return 1;
    }

    default boolean isFractional() {
        return false;
    }

}
