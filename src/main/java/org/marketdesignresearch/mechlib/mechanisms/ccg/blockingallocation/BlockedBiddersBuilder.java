package org.marketdesignresearch.mechlib.mechanisms.ccg.blockingallocation;

import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.mechanisms.ccg.constraintgeneration.PotentialCoalition;
import com.google.common.collect.ImmutableSet;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class BlockedBiddersBuilder {
    private final Set<Bidder> blockedBidders = new HashSet<>();
    private final Set<PotentialCoalition> blockingBidders = new HashSet<>();
    private BigDecimal blockingCoalitionValue = BigDecimal.ZERO;

    public boolean addBlockedBidder(Bidder bidder) {
        return blockedBidders.add(bidder);
    }

    public boolean addBlockedBidders(Collection<Bidder> bidders) {
        return blockedBidders.addAll(bidders);
    }

    public BlockedBidders build() {
        return new BlockedBidders(ImmutableSet.copyOf(blockedBidders), ImmutableSet.copyOf(blockingBidders), blockingCoalitionValue);
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
