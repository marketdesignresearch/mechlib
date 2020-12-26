package org.marketdesignresearch.mechlib.core.bidder.valuefunction.transform;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBid;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.BidTransformableValueFunction;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.ValueFunction;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class ShaveTransformation implements ComparableTransformation<ShaveTransformation> {
    public static final ShaveTransformation TRUTHFUL = new ShaveTransformation(BigDecimal.ONE);
    public static final ShaveTransformation ZERO = new ShaveTransformation(BigDecimal.ZERO);
    private final BigDecimal shaveFactor;

    public ShaveTransformation(BigDecimal shaveFactor) {
        this.shaveFactor = shaveFactor;
    }

    @Override
    public BundleExactValueBid apply(BidTransformableValueFunction combinatorialValueFunction) {
        return combinatorialValueFunction.toBid(v -> v.multiply(shaveFactor));
    }

    @Override
    public double getStrategyFactor() {
        return shaveFactor.doubleValue();
    }

    @Override
    public BigDecimal getExactStrategy() {
        return shaveFactor;
    }

    @Override
    public ShaveTransformation moreTruthful(BigDecimal strategyChange) {
        return from(shaveFactor.add(strategyChange));
    }


    @Override
    public ShaveTransformation merge(ShaveTransformation other) {
        return from(shaveFactor.add(other.shaveFactor).divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP));
    }

    @Override
    public String toString() {
        return "ShaveStrategy[" + shaveFactor.stripTrailingZeros() + "]";
    }


    @Override
    public int compareTo(ShaveTransformation o) {
        return shaveFactor.compareTo(o.shaveFactor);
    }

    public static ShaveTransformation from(double shaveFactor) {
        return from(BigDecimal.valueOf(shaveFactor).round(MathContext.DECIMAL32));
    }

    public static ShaveTransformation from(BigDecimal shaveFactor) {
        if (shaveFactor.compareTo(BigDecimal.ONE) == 0) {
            return TRUTHFUL;
        } else if (shaveFactor.compareTo(BigDecimal.ZERO) == 0) {
            return ZERO;
        } else {
            return new ShaveTransformation(shaveFactor);
        }
    }

}