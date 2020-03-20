package org.marketdesignresearch.mechlib.core.bid.bundle;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;

public class BundleExactValueBid extends BundleValueBid<BundleExactValuePair>{
	public static BundleExactValueBid singleBundleBid(BundleExactValuePair bundleBid) {
        return new BundleExactValueBid(ImmutableSet.of(bundleBid));
    }
    
    public BundleExactValueBid() {
		super();
	}

    public BundleExactValueBid(Set<BundleExactValuePair> of) {
		super(of);
	}

	public BundleExactValueBid reducedBy(BigDecimal payoff) {
		LinkedHashSet<BundleExactValuePair> newBids = getBundleBids().stream().map(bid -> bid.reducedBy(payoff)).collect(Collectors.toCollection(LinkedHashSet::new));
        return new BundleExactValueBid(newBids);
    }
    
    public BundleExactValueBid join(BundleValueBid<BundleExactValuePair> other) {
    	BundleExactValueBid result = new BundleExactValueBid();
        getBundleBids().forEach(result::addBundleBid);
        for(BundleExactValuePair otherBid : other.getBundleBids()) {
        	if(this.getBidForBundle(otherBid.getBundle()) != null) {
        		otherBid = otherBid.joinWith(this.getBidForBundle(otherBid.getBundle()));
        	}
        	result.addBundleBid(otherBid);
        }
        return result;
    }
}
