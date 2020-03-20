package org.marketdesignresearch.mechlib.core.bidder.strategy;

import java.math.BigDecimal;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBid;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.ValueFunction;

public interface Strategy {
    BundleExactValueBid apply(ValueFunction combinatorialValueFunction);

    Strategy ZERO = value -> value.toBid(b -> BigDecimal.ZERO);
    Strategy TRUTHFUL = ValueFunction::toBid;


    @SuppressWarnings("unchecked")
    static <T extends ComparableStrategy<?>> T strategy(BigDecimal strategyFactor, Class<T> strategyClass) {
        if (strategyClass.isAssignableFrom(ShaveStrategy.class)) {
            return (T) ShaveStrategy.from(strategyFactor);
        } else if (strategyClass.isAssignableFrom(AdditiveShaveStrategy.class)) {
            return (T) AdditiveShaveStrategy.from(strategyFactor);
        } else {
            throw new UnsupportedOperationException("Strategy " + strategyClass + " not supported");
        }
    }


    @SuppressWarnings("unchecked")
    static <T extends ComparableStrategy<?>> T strategy(double strategyFactor, Class<T> strategyClass) {
        if (strategyClass.isAssignableFrom(ShaveStrategy.class)) {
            return (T) ShaveStrategy.from(strategyFactor);
        } else if (strategyClass.isAssignableFrom(AdditiveShaveStrategy.class)) {
            return (T) AdditiveShaveStrategy.from(strategyFactor);
        } else {
            throw new UnsupportedOperationException("Strategy " + strategyClass + " not supported");
        }
    }


    @SuppressWarnings("unchecked")
    static <T extends Strategy> T truthful(Class<T> strategyClass) {
        if (strategyClass.isAssignableFrom(ShaveStrategy.class)) {
            return (T) ShaveStrategy.TRUTHFUL;
        } else if (strategyClass.isAssignableFrom(AdditiveShaveStrategy.class)) {
            return (T) AdditiveShaveStrategy.TRUTHFUL;
        } else if (strategyClass.isAssignableFrom(AdditiveOverbiddingStrategy.class)) {
            return (T) AdditiveOverbiddingStrategy.TRUTHFUL;
        }

        return (T) TRUTHFUL;
    }
}
