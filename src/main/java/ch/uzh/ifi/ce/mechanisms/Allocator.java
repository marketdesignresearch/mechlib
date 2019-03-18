package ch.uzh.ifi.ce.mechanisms;

import ch.uzh.ifi.ce.domain.Allocation;
import ch.uzh.ifi.ce.domain.Good;

/**
 * This interface represents a Mechanism that produces an {@link Allocation} of
 * {@link Good}s
 * 
 * @author Benedikt Buenz
 *
 */
public interface Allocator {
    Allocation getAllocation();
}
