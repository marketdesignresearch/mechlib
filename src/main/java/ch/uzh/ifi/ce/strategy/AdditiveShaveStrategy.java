package ch.uzh.ifi.ce.strategy;

import ch.uzh.ifi.ce.domain.Bid;
import ch.uzh.ifi.ce.domain.bidder.Value;
import ch.uzh.ifi.ce.utils.PrecisionUtils;

import java.math.BigDecimal;
import java.math.MathContext;

public class AdditiveShaveStrategy implements ComparableStrategy<AdditiveShaveStrategy> {
    public static final AdditiveShaveStrategy TRUTHFUL = new AdditiveShaveStrategy(BigDecimal.ZERO);
    private final BigDecimal additiveShave;

    public AdditiveShaveStrategy(BigDecimal additiveShave) {
        this.additiveShave = additiveShave;
    }

    @Override
    public Bid apply(Value combinatorialValue) {
        return combinatorialValue.toBid(b -> b.add(additiveShave).max(PrecisionUtils.EPSILON));
    }

    @Override
    public BigDecimal getExactStrategy() {
        return additiveShave;
    }

    @Override
    public AdditiveShaveStrategy moreTruthful(BigDecimal strategyChange) {
        return from(additiveShave.add(strategyChange));
    }



    @Override
    public double getStrategyFactor() {
        return additiveShave.doubleValue();
    }

    @Override
    public String toString() {
        return "AdditiveShave[" + additiveShave + "]";
    }

    @Override
    public int compareTo(AdditiveShaveStrategy o) {
        return additiveShave.compareTo(o.additiveShave);
    }

    @Override
    public AdditiveShaveStrategy merge(AdditiveShaveStrategy other) {
        return from(additiveShave.add(other.additiveShave).divide(BigDecimal.valueOf(2)));
    }

    public static AdditiveShaveStrategy from(double shaveFactor) {
        if (shaveFactor == 0) {
            return TRUTHFUL;
        } else {
            return new AdditiveShaveStrategy(BigDecimal.valueOf(shaveFactor).round(MathContext.DECIMAL32));
        }
    }


    public static AdditiveShaveStrategy from(BigDecimal shaveFactor) {
        if (shaveFactor.compareTo(BigDecimal.ZERO) == 0) {
            return TRUTHFUL;
        } else {
            return new AdditiveShaveStrategy(shaveFactor);
        }
    }

}
