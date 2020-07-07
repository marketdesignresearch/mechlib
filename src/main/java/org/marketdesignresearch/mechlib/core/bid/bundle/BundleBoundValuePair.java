package org.marketdesignresearch.mechlib.core.bid.bundle;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Good;
import org.springframework.data.annotation.PersistenceConstructor;

import com.google.common.base.Preconditions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BundleBoundValuePair extends BundleExactValuePair {

	@Getter
	private final BigDecimal upperBound;

	/**
	 * @param amount Bid amount
	 * @param bundle Goods to bid on
	 * @param id     Same id as BundleValue
	 */
	public BundleBoundValuePair(BigDecimal lowerBound, BigDecimal upperBound, Set<Good> bundle, String id) {
		this(lowerBound, upperBound, Bundle.of(bundle), id);
	}

	public BundleBoundValuePair(BundleExactValuePair exactBid) {
		this(exactBid.getAmount(), exactBid.getAmount(), exactBid.getBundle(), UUID.randomUUID().toString());
	}

	@PersistenceConstructor
	public BundleBoundValuePair(BigDecimal lowerBound, BigDecimal upperBound, Bundle bundle, String id) {
		super(lowerBound, bundle, id);
		this.upperBound = upperBound;
	}

	public BigDecimal getLowerBound() {
		return this.getAmount();
	}

	public BigDecimal getSpread() {
		return this.getUpperBound().subtract(this.getLowerBound());
	}

	BundleBoundValuePair joinWith(BundleExactValuePair otherBid) {
		BigDecimal otherUpperBound = (otherBid instanceof BundleBoundValuePair)
				? ((BundleBoundValuePair) otherBid).getUpperBound()
				: otherBid.getAmount();
		Preconditions.checkArgument(this.getBundle().equals(otherBid.getBundle()));
		Preconditions.checkArgument(this.getLowerBound().compareTo(otherUpperBound) <= 0);
		Preconditions.checkArgument(otherBid.getAmount().compareTo(this.getUpperBound()) <= 0);

		return new BundleBoundValuePair(this.getLowerBound().max(otherBid.getAmount()),
				this.getUpperBound().min(otherUpperBound), this.getBundle(), UUID.randomUUID().toString());
	}

	@Override
	public BundleBoundValuePair reducedBy(BigDecimal amount) {
		return new BundleBoundValuePair(this.getLowerBound().subtract(amount).max(BigDecimal.ZERO),
				this.getUpperBound().subtract(amount).max(BigDecimal.ZERO), this.getBundle(), getId());
	}

	public BundleBoundValuePair multiply(BigDecimal amount) {
		return new BundleBoundValuePair(getLowerBound().multiply(amount), getUpperBound().multiply(amount), getBundle(),
				getId());
	}
}
