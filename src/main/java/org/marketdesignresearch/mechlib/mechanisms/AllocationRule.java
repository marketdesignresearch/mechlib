package org.marketdesignresearch.mechlib.mechanisms;

import org.marketdesignresearch.mechlib.domain.Allocation;
import org.marketdesignresearch.mechlib.domain.Good;

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
