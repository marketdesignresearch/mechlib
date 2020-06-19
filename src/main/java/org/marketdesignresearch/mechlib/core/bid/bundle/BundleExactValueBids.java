package org.marketdesignresearch.mechlib.core.bid.bundle;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.bidder.ORBidder;
import org.marketdesignresearch.mechlib.core.bidder.XORBidder;
import org.marketdesignresearch.mechlib.core.bidder.strategy.Strategy;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.ValueFunction;

import com.google.common.collect.Sets;

public class BundleExactValueBids extends BundleValueBids<BundleExactValueBid> {

	public BundleExactValueBids() {
		super();
	}

	public BundleExactValueBids(Map<Bidder, BundleExactValueBid> bidMap) {
		super(bidMap);
	}

	@Override
	protected BundleExactValueBid createEmptyBid() {
		return new BundleExactValueBid();
	}

	// TODO: Does not work with OR*
	/**
	 *
	 * @return New bids, but reduced by the payoff of outcome
	 *
	 */
	public BundleExactValueBids reducedBy(Outcome outcome) {
		BundleExactValueBids newBids = new BundleExactValueBids();
		for (Map.Entry<Bidder, BundleExactValueBid> entry : getBidMap().entrySet()) {
			BigDecimal payoff = outcome.payoffOf(entry.getKey());
			newBids.setBid(entry.getKey(), entry.getValue().reducedBy(payoff));
		}
		return newBids;
	}

	/**
	 * Gives truthful bids
	 */
	public static BundleExactValueBids fromXORBidders(List<? extends XORBidder> bidders) {
		return fromXORBidders(bidders, Strategy.TRUTHFUL::apply);
	}

	public static BundleExactValueBids fromXORBidders(List<? extends XORBidder> bidders,
			Function<ValueFunction, BundleExactValueBid> operator) {
		Map<Bidder, BundleExactValueBid> bidMap = new HashMap<>();
		for (XORBidder bidder : bidders) {
			bidMap.put(bidder, operator.apply(bidder.getValue()));
		}
		return new BundleExactValueBids(bidMap);
	}

	public static BundleExactValueBids fromORBidders(List<? extends ORBidder> bidders) {
		return fromORBidders(bidders, Strategy.TRUTHFUL::apply);
	}

	@Override
	public BundleExactValueBids join(BundleValueBids<?> other) {
		BundleExactValueBids result = new BundleExactValueBids();
		Set<Bidder> bidders = Sets.union(getBidders(), other.getBidders());
		bidders.forEach(b -> {
			BundleExactValueBid joined = new BundleExactValueBid();
			if (getBid(b) != null)
				joined = joined.join(getBid(b));
			if (other.getBid(b) != null)
				joined = joined.join(other.getBid(b));
			result.setBid(b, joined);
		});
		return result;
	}

	@Override
	public BundleExactValueBids of(Set<? extends Bidder> bidders) {
		return new BundleExactValueBids(this.getBidMap().entrySet().stream().filter(b-> bidders.contains(b.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1,e2)->e1,LinkedHashMap::new)));
	}

	@Override
	public BundleExactValueBids without(Bidder bidder) {
		return new BundleExactValueBids(this.getBidMap().entrySet().stream().filter(b -> !b.getKey().equals(bidder)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1,e2)->e1,LinkedHashMap::new)));
	}

	public static BundleExactValueBids fromORBidders(List<? extends ORBidder> bidders,
			Function<ValueFunction, BundleExactValueBid> operator) {
		Map<Bidder, BundleExactValueBid> bidMap = new HashMap<>();
		for (ORBidder bidder : bidders) {
			bidMap.put(bidder, operator.apply(bidder.getValue()));
		}
		return new BundleExactValueBids(bidMap);
	}

	@Override
	public BundleExactValueBids multiply(BigDecimal scale) {
		BundleExactValueBids newBids = new BundleExactValueBids();
		for (Map.Entry<Bidder, BundleExactValueBid> entry : getBidMap().entrySet()) {
			newBids.setBid(entry.getKey(), entry.getValue().multiply(scale));
		}
		return newBids;
	}
}
