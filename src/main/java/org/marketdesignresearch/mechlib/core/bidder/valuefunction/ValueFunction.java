package org.marketdesignresearch.mechlib.core.bidder.valuefunction;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.function.UnaryOperator;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;

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
	BigDecimal getValueFor(Bundle bundle);

	/**
	 * Turns the value function into a {@link BundleValueBid}, given a
	 * {@link UnaryOperator} to account for a strategy.
	 *
	 * @param bundleBidOperator the unary operator to be applied on the bundle
	 *                          values to get bundle bids
	 * @return the bid
	 */
	BundleExactValueBid toBid(UnaryOperator<BigDecimal> bundleBidOperator);

	/**
	 * Turns the value function into a {@link BundleValueBid}, using true values.
	 *
	 * @return the bid
	 */
	default BundleExactValueBid toBid() {
		return toBid(UnaryOperator.identity());
	}

	WinnerDetermination toWDP(Bidder bidder);
}
