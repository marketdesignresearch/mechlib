package ch.uzh.ifi.ce.strategy;

import ch.uzh.ifi.ce.domain.Bid;
import ch.uzh.ifi.ce.domain.bidder.Value;

import java.math.BigDecimal;
import java.math.MathContext;

public class ShaveStrategy implements ComparableStrategy<ShaveStrategy> {
    public static final ShaveStrategy TRUTHFUL = new ShaveStrategy(BigDecimal.ONE);
    public static final ShaveStrategy ZERO = new ShaveStrategy(BigDecimal.ZERO);
    private final BigDecimal shaveFactor;

    public ShaveStrategy(BigDecimal shaveFactor) {
        this.shaveFactor = shaveFactor;
    }

    @Override
    public Bid apply(Value combinatorialValue) {

        return combinatorialValue.toBid(v -> v.multiply(shaveFactor));
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
        return from(shaveFactor.add(other.shaveFactor).divide(BigDecimal.valueOf(2)));
    }

    @Override
    public int hashCode() {
        return Double.hashCode(shaveFactor.doubleValue());
    }


    @Override
    public boolean equals(Object other) {

        if (shaveFactor.compareTo(BigDecimal.ONE) == 0 && other.equals(Strategy.TRUTHFUL)) {
            return true;
        }
        ShaveStrategy otherStrategy = (ShaveStrategy) other;
        return shaveFactor.compareTo(otherStrategy.shaveFactor) == 0;
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
        if (shaveFactor == 1) {
            return TRUTHFUL;
        } else if (shaveFactor == 0) {
            return ZERO;
        } else {
            return new ShaveStrategy(BigDecimal.valueOf(shaveFactor).round(MathContext.DECIMAL32));
        }
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
