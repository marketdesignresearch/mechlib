package org.marketdesignresearch.mechlib.core.bid.bundle;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;

import com.google.common.collect.Sets;

public class BundleBoundValueBids extends BundleValueBids<BundleBoundValueBid> {

	public BundleBoundValueBids() {
		super();
	}

	public BundleBoundValueBids(Map<Bidder, BundleBoundValueBid> bids) {
		super(bids);
	}

	@Override
	public BundleBoundValueBids of(Set<Bidder> bidders) {
		return new BundleBoundValueBids(this.getBidMap().entrySet().stream().filter(b -> bidders.contains(b.getKey()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new)));
	}

	@Override
	public BundleBoundValueBids without(Bidder bidder) {
		return new BundleBoundValueBids(this.getBidMap().entrySet().stream().filter(b -> !b.getKey().equals(bidder))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new)));
	}
	
	@Override
	public BundleBoundValueBids only(Set<UUID> bidders) {
		return new BundleBoundValueBids(this.getBidMap().entrySet().stream().filter(b -> bidders.contains(b.getKey().getId()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new)));
	}

	@Override
	public BundleBoundValueBids join(BundleValueBids<?> other) {
		if (!(other instanceof BundleBoundValueBids))
			throw new IllegalArgumentException("Currently unable to join non BundleBoundValueBids");

		BundleBoundValueBids result = new BundleBoundValueBids();
		Set<Bidder> bidders = Sets.union(getBidders(), other.getBidders());
		bidders.forEach(b -> {
			BundleBoundValueBid joined = new BundleBoundValueBid();
			if (getBid(b) != null)
				joined = joined.join(getBid(b));
			if (other.getBid(b) != null)
				joined = joined.join(other.getBid(b));
			result.setBid(b, joined);
		});
		return result;
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

	public BundleExactValueBids getAlphaBids(BigDecimal alpha) {
		return new BundleExactValueBids(this.getBidMap().entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getAlphaBid(alpha))));
	}

	public BundleExactValueBids getPerturbedBids(Allocation allocation) {
		Map<Bidder, BundleExactValueBid> perturbedBids = new LinkedHashMap<>();
		for(Map.Entry<Bidder,BundleBoundValueBid> entry : this.getBidMap().entrySet()) {
			Bundle allocated = allocation.allocationOf(entry.getKey()).getBundle();
			perturbedBids.put(entry.getKey(), entry.getValue().getPerturbedBid(allocated));
		}
		
		return new BundleExactValueBids(perturbedBids);
	}

}
