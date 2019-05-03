package org.marketdesignresearch.mechlib.strategy;

import org.marketdesignresearch.mechlib.domain.Bid;
import org.marketdesignresearch.mechlib.domain.bidder.Value;

import java.math.BigDecimal;
import java.util.function.UnaryOperator;

public interface Strategy {
    Bid apply(Value combinatorialValue);

    Strategy ZERO = value -> value.toBid(b -> BigDecimal.ZERO);
    Strategy TRUTHFUL = value -> value.toBid(UnaryOperator.identity());


    @SuppressWarnings("unchecked")
    static <T extends ComparableStrategy> T strategy(BigDecimal strategyFactor, Class<T> strategyClass) {
        if (strategyClass.isAssignableFrom(ShaveStrategy.class)) {
           
            return (T) ShaveStrategy.from(strategyFactor);
        } else if (strategyClass.isAssignableFrom(AdditiveShaveStrategy.class)) {
            return (T) AdditiveShaveStrategy.from(strategyFactor);
        } else {
            throw new UnsupportedOperationException("Strategy " + strategyClass + " not supported");
        }

    }
    

    @SuppressWarnings("unchecked")
    static <T extends ComparableStrategy> T strategy(double strategyFactor, Class<T> strategyClass) {
        if (strategyClass.isAssignableFrom(ShaveStrategy.class)) {

            return (T) ShaveStrategy.from(strategyFactor);
        } else if (strategyClass.isAssignableFrom(AdditiveShaveStrategy.class)) {
            return (T) AdditiveShaveStrategy.from(strategyFactor);
        } else{
            throw new UnsupportedOperationException("Strategy " + strategyClass + " not supported");
        }

    }


    @SuppressWarnings("unchecked")
    static <T extends Strategy> T truthful(Class<T> strategyClass) {
        if (strategyClass.isAssignableFrom(ShaveStrategy.class)) {
            return (T) ShaveStrategy.TRUTHFUL;
        } else if (strategyClass.isAssignableFrom(AdditiveShaveStrategy.class)) {
            return (T) AdditiveShaveStrategy.TRUTHFUL;
        }else if (strategyClass.isAssignableFrom(AdditiveOverbiddingStrategy.class)) {
            return (T) AdditiveOverbiddingStrategy.TRUTHFUL;
        }

        return (T) TRUTHFUL;
    }
}
