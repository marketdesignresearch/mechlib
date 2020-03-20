package org.marketdesignresearch.mechlib.core.bid.bundle;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

public class BundleBoundValueBid extends BundleValueBid<BundleBoundValuePair>{


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
        for(BundleExactValuePair otherExBid : other.getBundleBids()) {
        	BundleBoundValuePair otherBid = (otherExBid instanceof BundleBoundValuePair) ? (BundleBoundValuePair) otherExBid : new BundleBoundValuePair(otherExBid);
        	if(this.getBidForBundle(otherBid.getBundle()) != null) {
        		otherBid = (BundleBoundValuePair) otherBid.joinWith(this.getBidForBundle(otherBid.getBundle()));
        	}
        	result.addBundleBid(otherBid);
        }
        return result;
	}

	@Override
	public BundleBoundValueBid reducedBy(BigDecimal payoff) {
		LinkedHashSet<BundleBoundValuePair> newBids = getBundleBids().stream().map(bid -> bid.reducedBy(payoff)).collect(Collectors.toCollection(LinkedHashSet::new));
        return new BundleBoundValueBid(newBids);
	}
	
}
