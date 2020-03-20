package org.marketdesignresearch.mechlib.core.bid.bundle;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.BundleEntry;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.outcomerules.ccg.constraintgeneration.PotentialCoalition;
import org.springframework.data.annotation.PersistenceConstructor;

import com.google.common.base.Preconditions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Class that represents a Bid of one {@link Bidder} on one bundle of
 * {@link Good}s in a Combinatorial Auction The object is immutable
 * </p>compareTo, equals and hashCode are all based on the id.
 * 
 * @author Benedikt Buenz
 * 
 */
@RequiredArgsConstructor(onConstructor = @__({@PersistenceConstructor}))
@Slf4j
@EqualsAndHashCode
@ToString
public class BundleExactValuePair {

    @Getter
    private final BigDecimal amount;
    @Getter
    private final Bundle bundle;
    @Getter
    private final String id;

    /**
     * @param amount Bid amount
     * @param bundle Goods to bid on
     * @param id Same id as BundleValue
     */
    public BundleExactValuePair(BigDecimal amount, Set<Good> bundle, String id) {
        this(amount, Bundle.of(bundle), id);
    }

    /**
     * @return The {@link Good}s that this Bid bids on
     */
    @Deprecated
    public Set<Good> getGoods() {
        if (bundle.getBundleEntries().stream().anyMatch(entry -> entry.getAmount() > 1)) {
            log.error("Retrieving simple bundle when there are quantities greater than 1 involved!");
        }
        return Collections.unmodifiableSet(bundle.getBundleEntries().stream().map(BundleEntry::getGood).collect(Collectors.toSet()));
    }

    public BundleExactValuePair reducedBy(BigDecimal amount) {
        return new BundleExactValuePair(getAmount().subtract(amount).max(BigDecimal.ZERO), bundle, id);
    }

    public BundleExactValuePair withAmount(BigDecimal amount) {
        return new BundleExactValuePair(amount, bundle, id);
    }

    public PotentialCoalition getPotentialCoalition(Bidder bidder) {
        return new PotentialCoalition(getGoods(), bidder, amount);
    }

    public int countGood(Good good) {
        return bundle.countGood(good);
    }


	BundleExactValuePair joinWith(BundleExactValuePair otherBid) {
    	Preconditions.checkArgument(otherBid.getClass().equals(BundleExactValuePair.class));
    	Preconditions.checkArgument(this.getBundle().equals(otherBid.getBundle()));
    	return new BundleExactValuePair(this.getAmount().max(otherBid.getAmount()), this.getBundle(), UUID.randomUUID().toString());
    }
}
