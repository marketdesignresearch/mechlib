package org.marketdesignresearch.mechlib.outcomerules.itemlevel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.BidderAllocation;
import org.marketdesignresearch.mechlib.core.BidderPayment;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.Payment;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValuePair;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.SingleItemBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.SingleItemBids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentation;
import org.marketdesignresearch.mechlib.metainfo.MetaInfo;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRule;
import org.marketdesignresearch.mechlib.outcomerules.itemlevel.tiebreaker.AlphabeticTieBreaker;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class SingleItemOutcomeRule implements OutcomeRule {

	protected final Set<SingleItemBids> bidsPerGood = new LinkedHashSet<>();

	public SingleItemOutcomeRule(BundleValueBids<?> bids) {
		for (Good good : bids.getGoods()) {
			this.bidsPerGood.add(bids.getBidsPerSingleGood(good));
		}
	}

	@Override
	public final Outcome getOutcome() {
		Outcome result = Outcome.NONE;
		for (SingleItemBids bids : bidsPerGood) {
			Iterator<SingleItemBid> iterator = bids.getDescendingHighestBids().iterator();
			if (!iterator.hasNext()) {
				return new Outcome(Payment.ZERO, Allocation.EMPTY_ALLOCATION);
			}
			List<SingleItemBid> firstBids = new ArrayList<>();
			firstBids.add(iterator.next());
			while (iterator.hasNext()) {
				SingleItemBid next = iterator.next();
				if (next.getBundleBid().getAmount().equals(firstBids.get(0).getBundleBid().getAmount())) {
					firstBids.add(next);
				} else {
					break;
				}
			}
			SingleItemBid firstBid = firstBids.stream().sorted((a, b) -> new AlphabeticTieBreaker().compare(a, b))
					.collect(Collectors.toList()).get(0);
			BundleExactValuePair winningBid = firstBid.getBundleBid();
			Bidder winner = firstBid.getBidder();
			BidderAllocation bidderAllocation = new BidderAllocation(winningBid.getAmount(),
					Sets.newHashSet(bids.getItem()), Sets.newHashSet(winningBid));
			Allocation allocation = new Allocation(ImmutableMap.of(winner, bidderAllocation), bids, new MetaInfo());
			Payment payment = new Payment(ImmutableMap.of(winner, getSingleItemPayment(bids)), new MetaInfo());
			result = result.merge(new Outcome(payment, allocation));
		}
		return result;
	}

	protected abstract BidderPayment getSingleItemPayment(SingleItemBids bids);

	@Override
	public void setMipInstrumentation(MipInstrumentation mipInstrumentation) {
		// No MIP is going to be run
	}
}
