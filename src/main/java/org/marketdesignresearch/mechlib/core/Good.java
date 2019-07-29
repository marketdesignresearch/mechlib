package org.marketdesignresearch.mechlib.core;


import java.io.Serializable;
import java.util.UUID;


/**
 * A good represents an item that is to be distributed in a mechanism.
 * It is uniquely identified by a UUID, but also has a name.
 * <br>
 */
public interface Good extends Serializable {

    /**
     * Gets the UUID of the item.
     *
     * @return the UUID of the item
     */
    UUID getUuid();

    /**
     * Gets the name of the item.
     *
     * @return the name of the item
     */
    String getName();

    /**
     * Gets the quantity of the good. In many cases, this will be 1; The good is a single-quantity good.
     *
     * @return the quantity of the good
     */
    default int getQuantity() {
        return 1;
    }

    /**
     * Checks whether the good is a fractional good or not. In many cases, this will be false.
     *
     * @return true if the good is a fractional good; else false
     */
    default boolean isFractional() {
        return false;
    }

    /**
     * Checks whether the good is a dummy good or not. In many cases, this will be false.
     *
     * @return rue if the good is a dummy good; else false
     */
    default boolean isDummyGood() {
        return false;
    }


}
