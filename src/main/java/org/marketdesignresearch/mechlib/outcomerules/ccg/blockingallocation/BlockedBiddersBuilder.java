package org.marketdesignresearch.mechlib.outcomerules.ccg.blockingallocation;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.outcomerules.ccg.constraintgeneration.PotentialCoalition;

import com.google.common.collect.ImmutableSet;

public class BlockedBiddersBuilder {
	private final Set<Bidder> blockedBidders = new LinkedHashSet<>();
	private final Set<PotentialCoalition> blockingBidders = new LinkedHashSet<>();
	private BigDecimal blockingCoalitionValue = BigDecimal.ZERO;

	public boolean addBlockedBidder(Bidder bidder) {
		return blockedBidders.add(bidder);
	}

	public boolean addBlockedBidders(Collection<Bidder> bidders) {
		return blockedBidders.addAll(bidders);
	}

	public BlockedBidders build() {
		return new BlockedBidders(ImmutableSet.copyOf(blockedBidders), ImmutableSet.copyOf(blockingBidders),
				blockingCoalitionValue);
	}

	public boolean addBlockingBidder(PotentialCoalition blockingBidder) {
		if (blockingBidders.add(blockingBidder)) {
			blockingCoalitionValue = blockingCoalitionValue.add(blockingBidder.getValue());
			return true;
		} else {
			return false;
		}

	}

}
