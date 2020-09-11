package org.marketdesignresearch.mechlib.core.bidder.valuefunction.transform;

import java.math.BigDecimal;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBid;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.ValueFunction;

public interface ValueTransformation {
    BundleExactValueBid apply(ValueFunction combinatorialValueFunction);

    ValueTransformation ZERO = value -> value.toBid(b -> BigDecimal.ZERO);
    ValueTransformation TRUTHFUL = ValueFunction::toBid;


    @SuppressWarnings("unchecked")
    static <T extends ComparableTransformation<?>> T strategy(BigDecimal strategyFactor, Class<T> strategyClass) {
        if (strategyClass.isAssignableFrom(ShaveTransformation.class)) {
            return (T) ShaveTransformation.from(strategyFactor);
        } else if (strategyClass.isAssignableFrom(AdditiveShaveTransformation.class)) {
            return (T) AdditiveShaveTransformation.from(strategyFactor);
        } else {
            throw new UnsupportedOperationException("Strategy " + strategyClass + " not supported");
        }
    }


    @SuppressWarnings("unchecked")
    static <T extends ComparableTransformation<?>> T strategy(double strategyFactor, Class<T> strategyClass) {
        if (strategyClass.isAssignableFrom(ShaveTransformation.class)) {
            return (T) ShaveTransformation.from(strategyFactor);
        } else if (strategyClass.isAssignableFrom(AdditiveShaveTransformation.class)) {
            return (T) AdditiveShaveTransformation.from(strategyFactor);
        } else {
            throw new UnsupportedOperationException("Strategy " + strategyClass + " not supported");
        }
    }


    @SuppressWarnings("unchecked")
    static <T extends ValueTransformation> T truthful(Class<T> strategyClass) {
        if (strategyClass.isAssignableFrom(ShaveTransformation.class)) {
            return (T) ShaveTransformation.TRUTHFUL;
        } else if (strategyClass.isAssignableFrom(AdditiveShaveTransformation.class)) {
            return (T) AdditiveShaveTransformation.TRUTHFUL;
        } else if (strategyClass.isAssignableFrom(AdditiveOverbiddingTransformation.class)) {
            return (T) AdditiveOverbiddingTransformation.TRUTHFUL;
        }

        return (T) TRUTHFUL;
    }
}