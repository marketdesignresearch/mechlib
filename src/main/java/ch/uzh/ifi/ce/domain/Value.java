package ch.uzh.ifi.ce.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public interface Value extends Serializable {

    Set<BundleValue> getBundleValues();

    /*
     * (non-Javadoc)
     * 
     * @see
     * ch.uzh.ifi.ce.cca.domain.Value#toBid(java.util.function.UnaryOperator)
     */
    default Bid toBid(UnaryOperator<BigDecimal> bundleBidOperator) {
        Set<BundleBid> bundleBids = getBundleValues().stream().map(bb -> bb.toBid(bundleBidOperator)).collect(Collectors.toCollection(LinkedHashSet::new));
        return new Bid(bundleBids);
    }

    ValueType getValueType();

    default BigDecimal valueOf(BidderAllocation bidderAllocation) {
        BigDecimal totalValue = BigDecimal.ZERO;
        for (BundleBid bundleBid : bidderAllocation.getAcceptedBids()) {
            BigDecimal bundleValue = getBundleValues().stream().filter(bv -> bundleBid.getId().equals(bv.getId())).findAny().map(BundleValue::getAmount).get();
            totalValue = totalValue.add(bundleValue);
        }
        return totalValue;
    }

}