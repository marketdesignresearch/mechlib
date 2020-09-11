package org.marketdesignresearch.mechlib.core.bidder.valuefunction.transform;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBid;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.ValueFunction;
import org.marketdesignresearch.mechlib.utils.PrecisionUtils;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class AdditiveShaveTransformation implements ComparableTransformation<AdditiveShaveTransformation> {
    public static final AdditiveShaveTransformation TRUTHFUL = new AdditiveShaveTransformation(BigDecimal.ZERO);
    private final BigDecimal additiveShave;

    public AdditiveShaveTransformation(BigDecimal additiveShave) {
        this.additiveShave = additiveShave;
    }

    @Override
    public BundleExactValueBid apply(ValueFunction combinatorialValueFunction) {
        return combinatorialValueFunction.toBid(b -> b.add(additiveShave).max(PrecisionUtils.EPSILON));
    }

    @Override
    public BigDecimal getExactStrategy() {
        return additiveShave;
    }

    @Override
    public AdditiveShaveTransformation moreTruthful(BigDecimal strategyChange) {
        return from(additiveShave.add(strategyChange));
    }


    @Override
    public double getStrategyFactor() {
        return additiveShave.doubleValue();
    }

    @Override
    public int compareTo(AdditiveShaveTransformation o) {
        return additiveShave.compareTo(o.additiveShave);
    }

    @Override
    public AdditiveShaveTransformation merge(AdditiveShaveTransformation other) {
        return from(additiveShave.add(other.additiveShave).divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP));
    }

    public static AdditiveShaveTransformation from(double shaveFactor) {
        return from(BigDecimal.valueOf(shaveFactor).round(MathContext.DECIMAL32));
    }


    public static AdditiveShaveTransformation from(BigDecimal shaveFactor) {
        if (shaveFactor.compareTo(BigDecimal.ZERO) == 0) {
            return TRUTHFUL;
        } else {
            return new AdditiveShaveTransformation(shaveFactor);
        }
    }
}