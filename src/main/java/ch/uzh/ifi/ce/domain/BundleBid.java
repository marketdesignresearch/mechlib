package ch.uzh.ifi.ce.domain;

import ch.uzh.ifi.ce.mechanisms.ccg.constraintgeneration.PotentialCoalition;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class that represents a Bid of one {@link Bidder} on one bundle of
 * {@link Good}s in a Combinatorial Auction The object is immutable
 * </p>compareTo, equals and hashCode are all based on the id.
 * 
 * @author Benedikt Buenz
 * 
 */
@RequiredArgsConstructor
@Slf4j
@EqualsAndHashCode(of = "id")
@ToString
public class BundleBid {

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
    public BundleBid(BigDecimal amount, Set<Good> bundle, String id) {
        this(amount, Bundle.singleGoods(bundle), id);
    }

    /**
     * @return The {@link Good}s that this Bid bids on
     */
    @Deprecated
    public Set<Good> getGoods() {
        if (bundle.values().stream().anyMatch(n -> n > 1)) {
            log.error("Retrieving simple bundle when there are quantities greater than 1 involved!");
        }
        return Collections.unmodifiableSet(bundle.keySet());
    }

    public BundleBid reducedBy(BigDecimal amount) {
        return new BundleBid(getAmount().subtract(amount).max(BigDecimal.ZERO), bundle, id);
    }

    public BundleBid withAmount(BigDecimal amount) {
        return new BundleBid(amount, bundle, id);
    }

    public PotentialCoalition getPotentialCoalition(Bidder bidder) {
        return new PotentialCoalition(bundle.keySet(), bidder, amount);
    }

}
