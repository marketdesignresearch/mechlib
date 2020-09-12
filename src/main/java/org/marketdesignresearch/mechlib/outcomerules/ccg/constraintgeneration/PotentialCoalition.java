package org.marketdesignresearch.mechlib.outcomerules.ccg.constraintgeneration;

import java.math.BigDecimal;
import java.util.Set;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.springframework.data.annotation.PersistenceConstructor;

/**
 * A potential coalition is not in conflict with itself and can thus form a
 * coalition
 * 
 * @author Benedikt
 *
 */
public class PotentialCoalition {

	private final Bundle bundle;
	private final Bidder bidder;
	private final BigDecimal value;

	public PotentialCoalition(Set<Good> goods, Bidder bidder, BigDecimal value) {
		this(Bundle.of(goods), bidder, value);
	}

	@PersistenceConstructor
	public PotentialCoalition(Bundle bundle, Bidder bidder, BigDecimal value) {
		this.bundle = bundle;
		this.bidder = bidder;
		this.value = value;
	}

	public Bundle getBundle() {
		return bundle;
	}

	public Bidder getBidder() {
		return bidder;
	}

	public BigDecimal getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "PotentialCoalition[bidder=" + bidder + " ,value=" + value + "]";
	}

}
