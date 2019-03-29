package ch.uzh.ifi.ce.mechanisms.ccg.constraintgeneration;

import ch.uzh.ifi.ce.domain.Bidder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Set;

public class AverageDistanceFromReference implements Comparable<AverageDistanceFromReference> {
    private final BigDecimal totalDistance;
    private final Set<Bidder> blockedBidders;

    public AverageDistanceFromReference(BigDecimal totalDistance, Set<Bidder> blockedBidders) {
        this.totalDistance = totalDistance;
        this.blockedBidders = blockedBidders;
    }

    public BigDecimal getAverageDistance() {
        return blockedBidders.isEmpty() ? BigDecimal.ZERO : totalDistance.divide(BigDecimal.valueOf(blockedBidders.size()), MathContext.DECIMAL64);
    }

    public AverageDistanceFromReference subtract(AverageDistanceFromReference other) {
        return new AverageDistanceFromReference(totalDistance.subtract(other.totalDistance), ImmutableSet.copyOf(Sets.difference(blockedBidders, other.blockedBidders)));
    }


    @Override
    public int compareTo(AverageDistanceFromReference o) {
        if (blockedBidders.isEmpty()) {
            return -1;
        } else if (o.blockedBidders.isEmpty()) {
            return 1;
        }
        return getAverageDistance().compareTo(o.getAverageDistance());
    }

    @Override
    public boolean equals(Object obj) {
        AverageDistanceFromReference otherADR = (AverageDistanceFromReference) obj;
        return totalDistance.compareTo(otherADR.totalDistance) == 0 && blockedBidders.equals(otherADR.blockedBidders);
    }

    @Override
    public int hashCode() {
        return blockedBidders.hashCode() ^ totalDistance.stripTrailingZeros().hashCode();
    }

    @Override
    public String toString() {
        return "ADR[totalDistance=" + totalDistance + " ,blockedBidders=" + blockedBidders + " ,averageDistance=" + getAverageDistance() + "]";
    }

    public Set<Bidder> getBlockedBidders() {
        return blockedBidders;
    }
}
