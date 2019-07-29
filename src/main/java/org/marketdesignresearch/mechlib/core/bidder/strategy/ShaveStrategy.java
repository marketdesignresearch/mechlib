package org.marketdesignresearch.mechlib.core.bidder.strategy;

import lombok.EqualsAndHashCode;
import org.marketdesignresearch.mechlib.core.bid.Bid;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.ValueFunction;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@EqualsAndHashCode
public class ShaveStrategy implements ComparableStrategy<ShaveStrategy> {
    public static final ShaveStrategy TRUTHFUL = new ShaveStrategy(BigDecimal.ONE);
    public static final ShaveStrategy ZERO = new ShaveStrategy(BigDecimal.ZERO);
    private final BigDecimal shaveFactor;

    public ShaveStrategy(BigDecimal shaveFactor) {
        this.shaveFactor = shaveFactor;
    }

    @Override
    public Bid apply(ValueFunction combinatorialValueFunction) {

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
    public ShaveStrategy moreTruthful(BigDecimal strategyChange) {
        return from(shaveFactor.add(strategyChange));
    }


    @Override
    public ShaveStrategy merge(ShaveStrategy other) {
        return from(shaveFactor.add(other.shaveFactor).divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP));
    }

    @Override
    public String toString() {
        return "ShaveStrategy[" + shaveFactor.stripTrailingZeros() + "]";
    }


    @Override
    public int compareTo(ShaveStrategy o) {
        return shaveFactor.compareTo(o.shaveFactor);
    }

    public static ShaveStrategy from(double shaveFactor) {
        return from(BigDecimal.valueOf(shaveFactor).round(MathContext.DECIMAL32));
    }

    public static ShaveStrategy from(BigDecimal shaveFactor) {
        if (shaveFactor.compareTo(BigDecimal.ONE) == 0) {
            return TRUTHFUL;
        } else if (shaveFactor.compareTo(BigDecimal.ZERO) == 0) {
            return ZERO;
        } else {
            return new ShaveStrategy(shaveFactor);
        }
    }

}
