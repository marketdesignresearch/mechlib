package org.marketdesignresearch.mechlib.core.bid.bundle;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Bundle;

public class BundleBoundValueBid extends BundleValueBid<BundleBoundValuePair> {

	public BundleBoundValueBid() {
		super();
	}

	public BundleBoundValueBid(LinkedHashSet<BundleBoundValuePair> newBids) {
		super(newBids);
	}

	@Override
	public BundleBoundValueBid join(BundleValueBid<?> other) {
		BundleBoundValueBid result = new BundleBoundValueBid();
		getBundleBids().forEach(result::addBundleBid);
		for (BundleExactValuePair otherExBid : other.getBundleBids()) {
			BundleBoundValuePair otherBid = (otherExBid instanceof BundleBoundValuePair)
					? (BundleBoundValuePair) otherExBid
					: new BundleBoundValuePair(otherExBid);
			if (this.getBidForBundle(otherBid.getBundle()) != null) {
				otherBid = (BundleBoundValuePair) otherBid.joinWith(this.getBidForBundle(otherBid.getBundle()));
			}
			result.addBundleBid(otherBid);
		}
		return result;
	}

	@Override
	public BundleBoundValueBid reducedBy(BigDecimal payoff) {
		LinkedHashSet<BundleBoundValuePair> newBids = getBundleBids().stream().map(bid -> bid.reducedBy(payoff))
				.collect(Collectors.toCollection(LinkedHashSet::new));
		return new BundleBoundValueBid(newBids);
	}

	@Override
	public BundleBoundValueBid multiply(BigDecimal scale) {
		LinkedHashSet<BundleBoundValuePair> newBids = getBundleBids().stream().map(bid -> bid.multiply(scale))
				.collect(Collectors.toCollection(LinkedHashSet::new));
		return new BundleBoundValueBid(newBids);
	}
	
	public BundleBoundValueBid ln() {
		LinkedHashSet<BundleBoundValuePair> newBids = getBundleBids().stream().map(bid -> bid.ln())
				.collect(Collectors.toCollection(LinkedHashSet::new));
		return new BundleBoundValueBid(newBids);
	}
	
	public BundleBoundValueBid exp() {
		LinkedHashSet<BundleBoundValuePair> newBids = getBundleBids().stream().map(bid -> bid.exp())
				.collect(Collectors.toCollection(LinkedHashSet::new));
		return new BundleBoundValueBid(newBids);
	}

	public BundleExactValueBid getAlphaBid(BigDecimal alpha) {
		return new BundleExactValueBid(this.getBundleBids().stream()
				.map(bid -> new BundleExactValuePair(
						bid.getLowerBound().multiply(alpha)
								.add(bid.getUpperBound().multiply(BigDecimal.ONE.subtract(alpha))),
						bid.getBundle(), bid.getId()))
				.collect(Collectors.toCollection(LinkedHashSet::new)));
	}

	public BundleExactValueBid getPerturbedBid(Bundle allocated) {
		return new BundleExactValueBid(this.getBundleBids().stream()
				.map(b -> new BundleExactValuePair(
						b.getBundle().equals(allocated) ? b.getLowerBound() : b.getUpperBound(), b.getBundle(),
						b.getId()))
				.collect(Collectors.toCollection(LinkedHashSet::new)));
	}

	public BundleBoundValueBid copy() {
		return new BundleBoundValueBid(new LinkedHashSet<>(this.getBundleBids()));
	}

	public BundleBoundValueBid add(BigDecimal scale) {
		LinkedHashSet<BundleBoundValuePair> newBids = getBundleBids().stream().map(bid -> bid.add(scale))
				.collect(Collectors.toCollection(LinkedHashSet::new));
		return new BundleBoundValueBid(newBids);
	}
}
