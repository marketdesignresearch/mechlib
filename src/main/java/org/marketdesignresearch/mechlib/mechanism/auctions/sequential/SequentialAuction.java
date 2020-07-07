package org.marketdesignresearch.mechlib.mechanism.auctions.sequential;

import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValuePair;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRuleGenerator;
import org.springframework.data.annotation.PersistenceConstructor;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * This class represents a sequential auction where for each round, there are
 * only bids placed for a specific good
 * 
 * TODO change to new auction design with interactions
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SequentialAuction extends Auction<BundleExactValuePair> {

	@PersistenceConstructor
	public SequentialAuction(Domain domain, OutcomeRuleGenerator outcomeRuleGenerator) {
		super(domain, outcomeRuleGenerator);
		setMaxRounds(domain.getGoods().size());
	}

	/*
	 * @Override public Map<Bidder, List<Bundle>> restrictedBids() { if (finished())
	 * return new HashMap<>(); Bundle bundle =
	 * Bundle.of(Sets.newHashSet(getDomain().getGoods().get(getNumberOfRounds())));
	 * Map<Bidder, List<Bundle>> map = new HashMap<>();
	 * getDomain().getBidders().forEach(bidder -> map.put(bidder,
	 * Lists.newArrayList(bundle))); return map; }
	 */

	/**
	 * The bidder will take into account previous round's allocations, because they
	 * are definitive in a sequential auction (not interim, as in other auctions).
	 *
	 * @param bidder the bidder that's about to propose a bid
	 * @return A (currently truthful) bid
	 */
	/*
	 * public BundleValueBid<BundleValuePair> proposeBid(Bidder bidder) {
	 * BundleValueBid bid = new BundleValueBid(); if (allowedNumberOfBids() > 0 &&
	 * restrictedBids().containsKey(bidder)) { Bundle alreadyWon = Bundle.EMPTY; for
	 * (int i = 0; i < getNumberOfRounds(); i++) { alreadyWon =
	 * alreadyWon.merge(getOutcomeAtRound(i).getAllocation().allocationOf(bidder).
	 * getBundle()); } for (Bundle bundle : restrictedBids().get(bidder)) {
	 * bid.addBundleBid(new BundleValuePair(bidder.getValue(bundle, alreadyWon),
	 * bundle, UUID.randomUUID().toString())); } } return bid; }
	 */

	/**
	 * Overrides the default method to have outcomes only based on each round's bids
	 */
	@Override
	public Outcome getOutcomeAtRound(OutcomeRuleGenerator generator, int index) {
		return generator.getOutcomeRule(getBidsAt(index), getMipInstrumentation()).getOutcome();
	}

	@Override
	public Outcome getOutcome(OutcomeRuleGenerator generator) {
		Outcome result = Outcome.NONE;
		for (int i = 0; i < getNumberOfRounds(); i++) {
			result = result.merge(getOutcomeAtRound(generator, i));
		}
		return result;
	}
}
