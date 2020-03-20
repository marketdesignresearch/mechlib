package org.marketdesignresearch.mechlib.core.bid.bundle;

public class BundleBoundValueBid extends BundleValueBid<BundleBoundValuePair>{

	// TODO check types
	@Override
	public BundleValueBid<BundleBoundValuePair> join(BundleValueBid<BundleBoundValuePair> other) {
		BundleBoundValueBid result = new BundleBoundValueBid();
        getBundleBids().forEach(result::addBundleBid);
        for(BundleBoundValuePair otherBid : other.getBundleBids()) {
        	if(this.getBidForBundle(otherBid.getBundle()) != null) {
        		otherBid = (BundleBoundValuePair) otherBid.joinWith(this.getBidForBundle(otherBid.getBundle()));
        	}
        	result.addBundleBid(otherBid);
        }
        return result;
	}
	
}
