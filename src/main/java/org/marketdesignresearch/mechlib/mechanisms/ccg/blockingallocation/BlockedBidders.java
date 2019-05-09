package org.marketdesignresearch.mechlib.mechanisms.ccg.blockingallocation;

import org.marketdesignresearch.mechlib.domain.bidder.Bidder;
import org.marketdesignresearch.mechlib.domain.BidderPayment;
import org.marketdesignresearch.mechlib.domain.Payment;
import org.marketdesignresearch.mechlib.mechanisms.ccg.constraintgeneration.PotentialCoalition;
import com.google.common.collect.ImmutableSet;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class BlockedBidders {
    private final Set<Bidder> blockedBidders;
    private final Set<PotentialCoalition> blockingCoalition;
    private final Set<Bidder> nonTraitors;
    private final BigDecimal blockingCoalitionValue;

    private static final BlockedBidders EMPTY_BLOCKED_BIDDERS = new BlockedBidders(Collections.emptySet(), Collections.emptySet(), BigDecimal.ZERO);

    public BlockedBidders(Set<Bidder> blockedBidders, Set<PotentialCoalition> blockingBidders, BigDecimal blockingCoalitionValue) {
        this.blockedBidders = ImmutableSet.copyOf(blockedBidders);
        this.blockingCoalitionValue = blockingCoalitionValue;
        this.blockingCoalition = blockingBidders;

        Set<Bidder> nonTraitors = new HashSet<>(blockedBidders);
        blockingBidders.forEach(pc -> nonTraitors.remove(pc.getBidder()));
        this.nonTraitors = nonTraitors;
    }

    public BigDecimal getBlockingCoalitionValue() {
        return blockingCoalitionValue;
    }

    public Set<Bidder> getBlockedBidders() {
        return blockedBidders;
    }

    public Set<Bidder> getNonTraitors() {
        return nonTraitors;
    }

    /**
     * See Day Raghavan for detailed explanation
     * 
     * @return the coalitional value of the blocking bidders minus the total
     *         payment of the traitors
     */
    public BigDecimal getBlockedAmount(Payment lastPayment) {
        // Traitors:= bidders (W) \cap blockingBidders (C)
        BigDecimal traitorsPayment = blockingCoalition.stream().map(PotentialCoalition::getBidder).map(lastPayment::paymentOf).map(BidderPayment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        return blockingCoalitionValue.subtract(traitorsPayment);
    }

    public Set<PotentialCoalition> getBlockingBidders() {
        return blockingCoalition;
    }

    public static BlockedBidders emptyBlockedBidders() {
        return EMPTY_BLOCKED_BIDDERS;
    }

    public static BlockedBidders from(Set<PotentialCoalition> graph, Set<PotentialCoalition> blockingAllocation) {
        BlockedBiddersBuilder builder = new BlockedBiddersBuilder();
        for (PotentialCoalition vertex : graph) {
            // In C
            if (blockingAllocation.contains(vertex)) {
                builder.addBlockingBidder(vertex);
            }// In W
            else {
                builder.addBlockedBidder(vertex.getBidder());
            }
        }
        return builder.build();
    }

    @Override
    public String toString() {
        return "Blocked bidders: " + blockedBidders + " with coalitional value of " + blockingCoalitionValue;
    }
}
