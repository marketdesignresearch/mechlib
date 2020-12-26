package org.marketdesignresearch.mechlib.core.bidder.valuefunction;

import java.io.Serializable;
import java.math.BigDecimal;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;

/**
 * Attached to a {@link Bidder}, this interface is often used to represent the
 * underlying value function, to abstract away the value query.
 */
public interface ValueFunction extends Serializable {

	/**
	 * Gets value for a bundle. 
	 *
	 * @param bundle the bundle
	 * @return the value for this bundle
	 */
	BigDecimal getValue(Bundle bundle);
}
