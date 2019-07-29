package org.marketdesignresearch.mechlib.mechanisms;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.Good;

/**
 * This interface represents a Mechanism that produces an {@link Allocation} of
 * {@link Good}s
 * 
 * @author Benedikt Buenz
 *
 */
public interface AllocationRule {
    Allocation getAllocation();
}
