package org.marketdesignresearch.mechlib.mechanism.auctions.pvm.ml;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValuePair;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.BundleValue;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.ORValueFunction;
import org.springframework.data.annotation.PersistenceConstructor;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * This dummy algorithm adds an over-valued value to the currently known reports.
 * This only serves to keep PVM from being "wrong" about the bidder's value function, which leads
 * to new queries.
 */
/*@AllArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@PersistenceConstructor}))
@RequiredArgsConstructor
@Slf4j
public class LinearRegressionMLAlgorithm implements MLAlgorithm {
    private static MathContext precision = new MathContext(6);
    private final List<? extends Good> goods;
    private BundleValueBid<BundleExactValuePair> reports = new BundleValueBid<>();

    public void addReport(BundleValueBid<BundleExactValuePair> report) {
        reports = reports.join(report);
    }

    public ORValueFunction inferValueFunction() {

            ArrayList<Double> yVector = new ArrayList<>();
            ArrayList<ArrayList<Double>> xVectors = new ArrayList<>();

            for (BundleExactValuePair report : reports.getBundleBids()) {
                if (!Bundle.EMPTY.equals(report.getBundle())) {
                    yVector.add(report.getAmount().doubleValue());
                    ArrayList<Double> xVector = new ArrayList<>();
                    for (Good good : goods) {
                        xVector.add((double) report.getBundle().countGood(good));
                    }
                    xVectors.add(xVector);
                }
            }

            OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
            regression.setNoIntercept(true);
            double[] yArray = new double[yVector.size()];
            for (int i = 0; i < yVector.size(); i++) {
                yArray[i] = yVector.get(i);
            }
            double[][] xArrays = new double[xVectors.size()][xVectors.get(0).size()];
            for (int i = 0; i < xVectors.size(); i++) {
                for (int j = 0; j < xVectors.get(i).size(); j++) {
                    xArrays[i][j] = xVectors.get(i).get(j);
                }
            }

            regression.newSampleData(yArray, xArrays);
            double[] betas = regression.estimateRegressionParameters();

            Set<BundleValue> bundleValues = new HashSet<>();
            for (int i = 0; i < goods.size(); i++) {
                bundleValues.add(new BundleValue(BigDecimal.valueOf(betas[i]).round(precision), Bundle.of(goods.get(i))));
            }

            return new ORValueFunction(bundleValues);

    }
}
*/