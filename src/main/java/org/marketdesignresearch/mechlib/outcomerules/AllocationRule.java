package org.marketdesignresearch.mechlib.outcomerules;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentationable;

/**
 * This interface represents a rule to allocate {@link Good}s to {@link Bidder}s
 *
 */
public interface AllocationRule extends MipInstrumentationable {
	Allocation getAllocation();
}
