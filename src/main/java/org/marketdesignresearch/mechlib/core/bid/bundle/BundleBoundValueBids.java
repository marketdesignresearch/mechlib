package org.marketdesignresearch.mechlib.core.bid.bundle;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;

public class BundleBoundValueBids extends BundleValueBids<BundleBoundValueBid>{

	public BundleBoundValueBids() {
		super();
	}
	
	public BundleBoundValueBids(Map<Bidder, BundleBoundValueBid> bids) {
		super(bids);
	}

	@Override
	public BundleBoundValueBids of(Set<? extends Bidder> bidders) {
		return new BundleBoundValueBids(this.getBidMap().entrySet().stream().filter(b-> bidders.contains(b.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,(e1,e2)->e1,LinkedHashMap::new)));
	}

	@Override
	public BundleBoundValueBids without(Bidder bidder) {
		return new BundleBoundValueBids(this.getBidMap().entrySet().stream().filter(b -> !b.getKey().equals(bidder)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,(e1,e2)->e1,LinkedHashMap::new)));
	}

	@Override
	public BundleValueBids<BundleBoundValueBid> join(BundleValueBids<?> other) {
		
		// TODO Auto-generated method stub
		return null;
	}

	public BundleBoundValueBids reducedBy(Outcome outcome) {
		BundleBoundValueBids newBids = new BundleBoundValueBids();
		for (Map.Entry<Bidder, BundleBoundValueBid> entry : getBidMap().entrySet()) {
			BigDecimal payoff = outcome.payoffOf(entry.getKey());
			newBids.setBid(entry.getKey(), entry.getValue().reducedBy(payoff));
		}
		return newBids;
	}

	@Override
	protected BundleBoundValueBid createEmptyBid() {
		return new BundleBoundValueBid();
	}

	@Override
	public BundleValueBids<BundleBoundValueBid> multiply(BigDecimal scale) {
		BundleBoundValueBids newBids = new BundleBoundValueBids();
		for (Map.Entry<Bidder, BundleBoundValueBid> entry : getBidMap().entrySet()) {
			newBids.setBid(entry.getKey(), entry.getValue().multiply(scale));
		}
		return newBids;
	}

}
