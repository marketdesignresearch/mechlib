package org.marketdesignresearch.mechlib.outcomerules.ccg;

import static org.assertj.core.api.Assertions.assertThat;


import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Payment;
import org.marketdesignresearch.mechlib.core.SimpleGood;
import org.marketdesignresearch.mechlib.core.SimpleXORDomain;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.core.bidder.XORBidder;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.BundleValue;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.XORValueFunction;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRule;
import org.marketdesignresearch.mechlib.outcomerules.ccg.blockingallocation.XORBlockingCoalitionFinderFactory;
import org.marketdesignresearch.mechlib.outcomerules.ccg.constraintgeneration.ConstraintGenerationAlgorithm;
import org.marketdesignresearch.mechlib.outcomerules.ccg.paymentrules.EqualWeightsFactory;
import org.marketdesignresearch.mechlib.outcomerules.ccg.paymentrules.InversePayoffWeightsFactory;
import org.marketdesignresearch.mechlib.outcomerules.ccg.paymentrules.Norm;
import org.marketdesignresearch.mechlib.outcomerules.ccg.paymentrules.NormFactory;
import org.marketdesignresearch.mechlib.outcomerules.ccg.paymentrules.PayoffWeightsFactory;
import org.marketdesignresearch.mechlib.outcomerules.ccg.paymentrules.VariableNormCCGFactory;
import org.marketdesignresearch.mechlib.outcomerules.ccg.referencepoint.BidsReferencePointFactory;
import org.marketdesignresearch.mechlib.outcomerules.ccg.referencepoint.VCGReferencePointFactory;
import org.marketdesignresearch.mechlib.outcomerules.vcg.ORVCGRule;
import org.marketdesignresearch.mechlib.utils.CPLEXUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class PaymentRuleTests {
    @Test
    public void testSimpleExample() {
        CPLEXUtils.SOLVER.exampleNormSolveParams();
        SimpleGood west = new SimpleGood("west", 2, false);

        BundleValue valueWest1 = new BundleValue(BigDecimal.valueOf(2), Bundle.of(west));
        XORBidder westBidder1 = new XORBidder("west1", new XORValueFunction(ImmutableSet.of(valueWest1)));

        BundleValue valueWest2 = new BundleValue(BigDecimal.valueOf(2), Bundle.of(west));
        XORBidder westBidder2 = new XORBidder("west2", new XORValueFunction(ImmutableSet.of(valueWest2)));

        BundleValue globalBundle = new BundleValue(BigDecimal.valueOf(3), new Bundle(Map.of(west, 2)), "global");
        XORBidder globalBidder = new XORBidder("global", new XORValueFunction(ImmutableSet.of(globalBundle)));

        SimpleXORDomain domain = new SimpleXORDomain(ImmutableList.of(westBidder1, westBidder2, globalBidder), ImmutableList.of(west));
        MechanismFactory equalNorm = new VariableAlgorithmCCGFactory(new XORBlockingCoalitionFinderFactory(), ConstraintGenerationAlgorithm.STANDARD_CCG);
        OutcomeRule outcomeRule = equalNorm.getOutcomeRule(BundleExactValueBids.fromXORBidders(domain.getBidders()));
        Payment payment = outcomeRule.getPayment();
        assertThat(payment.paymentOf(westBidder2).getAmount()).isEqualByComparingTo(BigDecimal.valueOf(1.5));
        assertThat(payment.paymentOf(westBidder1).getAmount()).isEqualByComparingTo(BigDecimal.valueOf(1.5));

    }

    @Test
    public void testEqualRule() {
        CPLEXUtils.SOLVER.exampleNormSolveParams();
        SimpleGood west = new SimpleGood("west");
        SimpleGood east = new SimpleGood("east");

        BundleValue westBundle = new BundleValue(BigDecimal.valueOf(1), ImmutableSet.of(west), "west");
        XORBidder westBidder = new XORBidder("west", new XORValueFunction(ImmutableSet.of(westBundle)));

        BundleValue eastBundle = new BundleValue(BigDecimal.valueOf(2.5), ImmutableSet.of(east), "east");
        XORBidder eastBidder = new XORBidder("east", new XORValueFunction(ImmutableSet.of(eastBundle)));

        BundleValue globalBundle = new BundleValue(BigDecimal.valueOf(2), ImmutableSet.of(west, east), "global");
        XORBidder globalBidder = new XORBidder("global", new XORValueFunction(ImmutableSet.of(globalBundle)));

        SimpleXORDomain domain = new SimpleXORDomain(ImmutableList.of(westBidder, eastBidder, globalBidder), ImmutableList.of(west, east));
        MechanismFactory equalNorm = new VariableNormCCGFactory(new BidsReferencePointFactory(), new NormFactory(Norm.MANHATTAN, new EqualWeightsFactory(), Payment.ZERO),
                NormFactory.withEqualWeights(Norm.EUCLIDEAN));
        OutcomeRule outcomeRule = equalNorm.getOutcomeRule(BundleExactValueBids.fromXORBidders(domain.getBidders()));
        Payment payment = outcomeRule.getPayment();
        assertThat(payment.paymentOf(eastBidder).getAmount()).isEqualByComparingTo(BigDecimal.valueOf(1.75));
        assertThat(payment.paymentOf(westBidder).getAmount()).isEqualByComparingTo(BigDecimal.valueOf(.25));

    }

    @Test
    public void testEqualRuleGeneric() {
        CPLEXUtils.SOLVER.exampleNormSolveParams();
        SimpleGood west = new SimpleGood("west", 2, false);

        BundleValue valueWest1 = new BundleValue(BigDecimal.valueOf(1), Bundle.of(west));
        XORBidder westBidder1 = new XORBidder("west1", new XORValueFunction(ImmutableSet.of(valueWest1)));

        BundleValue valueWest2 = new BundleValue(BigDecimal.valueOf(2.5), Bundle.of(west));
        XORBidder westBidder2 = new XORBidder("west2", new XORValueFunction(ImmutableSet.of(valueWest2)));

        BundleValue globalBundle = new BundleValue(BigDecimal.valueOf(2), new Bundle(Map.of(west, 2)), "global");
        XORBidder globalBidder = new XORBidder("global", new XORValueFunction(ImmutableSet.of(globalBundle)));

        SimpleXORDomain domain = new SimpleXORDomain(ImmutableList.of(westBidder1, westBidder2, globalBidder), ImmutableList.of(west));
        MechanismFactory equalNorm = new VariableNormCCGFactory(new BidsReferencePointFactory(), new NormFactory(Norm.MANHATTAN, new EqualWeightsFactory(), Payment.ZERO),
                NormFactory.withEqualWeights(Norm.EUCLIDEAN));
        OutcomeRule outcomeRule = equalNorm.getOutcomeRule(BundleExactValueBids.fromXORBidders(domain.getBidders()));
        Payment payment = outcomeRule.getPayment();
        assertThat(payment.paymentOf(westBidder2).getAmount()).as("FIXME: This should produce the same result as above. Seems like something needs to be modified in the norms / reference points /...").isEqualByComparingTo(BigDecimal.valueOf(1.75));
        assertThat(payment.paymentOf(westBidder1).getAmount()).isEqualByComparingTo(BigDecimal.valueOf(.25));

    }
    
    
    @Test
    public void testEqualRulePerBidConstraintGenerator() {
        CPLEXUtils.SOLVER.exampleNormSolveParams();
        SimpleGood west = new SimpleGood("west");
        SimpleGood east = new SimpleGood("east");

        BundleValue westBundle = new BundleValue(BigDecimal.valueOf(1), ImmutableSet.of(west), "west");
        XORBidder westBidder = new XORBidder("west", new XORValueFunction(ImmutableSet.of(westBundle)));

        BundleValue eastBundle = new BundleValue(BigDecimal.valueOf(2.5), ImmutableSet.of(east), "east");
        XORBidder eastBidder = new XORBidder("east", new XORValueFunction(ImmutableSet.of(eastBundle)));

        BundleValue globalBundle = new BundleValue(BigDecimal.valueOf(2), ImmutableSet.of(west, east), "global");
        XORBidder globalBidder = new XORBidder("global", new XORValueFunction(ImmutableSet.of(globalBundle)));

        SimpleXORDomain domain = new SimpleXORDomain(ImmutableList.of(westBidder, eastBidder, globalBidder), ImmutableList.of(west, east));
        MechanismFactory equalNorm = new VariableNormCCGFactory(new BidsReferencePointFactory(), List.of(new NormFactory(Norm.MANHATTAN, new EqualWeightsFactory(), Payment.ZERO),
                NormFactory.withEqualWeights(Norm.EUCLIDEAN)), ConstraintGenerationAlgorithm.PER_BID_CONSTRAINTS);
        OutcomeRule outcomeRule = equalNorm.getOutcomeRule(BundleExactValueBids.fromXORBidders(domain.getBidders()));
        Payment payment = outcomeRule.getPayment();
        assertThat(payment.paymentOf(eastBidder).getAmount()).isEqualByComparingTo(BigDecimal.valueOf(1.75));
        assertThat(payment.paymentOf(westBidder).getAmount()).isEqualByComparingTo(BigDecimal.valueOf(.25));

    }

    @Test
    public void testEqualRuleGenericPerBidConstraintGenerator() {
        CPLEXUtils.SOLVER.exampleNormSolveParams();
        SimpleGood west = new SimpleGood("west", 2, false);

        BundleValue valueWest1 = new BundleValue(BigDecimal.valueOf(1), Bundle.of(west));
        XORBidder westBidder1 = new XORBidder("west1", new XORValueFunction(ImmutableSet.of(valueWest1)));

        BundleValue valueWest2 = new BundleValue(BigDecimal.valueOf(2.5), Bundle.of(west));
        XORBidder westBidder2 = new XORBidder("west2", new XORValueFunction(ImmutableSet.of(valueWest2)));

        BundleValue globalBundle = new BundleValue(BigDecimal.valueOf(2), new Bundle(Map.of(west, 2)), "global");
        XORBidder globalBidder = new XORBidder("global", new XORValueFunction(ImmutableSet.of(globalBundle)));

        SimpleXORDomain domain = new SimpleXORDomain(ImmutableList.of(westBidder1, westBidder2, globalBidder), ImmutableList.of(west));
        MechanismFactory equalNorm = new VariableNormCCGFactory(new BidsReferencePointFactory(), List.of(new NormFactory(Norm.MANHATTAN, new EqualWeightsFactory(), Payment.ZERO),
                NormFactory.withEqualWeights(Norm.EUCLIDEAN)), ConstraintGenerationAlgorithm.PER_BID_CONSTRAINTS);
        OutcomeRule outcomeRule = equalNorm.getOutcomeRule(BundleExactValueBids.fromXORBidders(domain.getBidders()));
        Payment payment = outcomeRule.getPayment();
        assertThat(payment.paymentOf(westBidder2).getAmount()).as("FIXME: This should produce the same result as above. Seems like something needs to be modified in the norms / reference points /...").isEqualByComparingTo(BigDecimal.valueOf(1.75));
        assertThat(payment.paymentOf(westBidder1).getAmount()).isEqualByComparingTo(BigDecimal.valueOf(.25));

    }

    @Test
    public void testLubinParkesNames() {
        ParameterizableCCGFactory mechFactory = new VariableNormCCGFactory(new BidsReferencePointFactory(), new NormFactory(Norm.MANHATTAN, new EqualWeightsFactory()),
                NormFactory.withEqualWeights(Norm.EUCLIDEAN));
        System.out.println(mechFactory.lubinParkesName());
    }

    @Test
    public void paperExample() throws IOException {
        CPLEXUtils.SOLVER.exampleNormSolveParams();
        BundleValueBids<?> bids = BundleExactValueBids.fromXORBidders(SimpleXORDomain.fromCatsFile(Paths.get("src/test/resources/supersimple.txt")).getBidders());
        MechanismFactory quadratic = new VariableNormCCGFactory(new VCGReferencePointFactory(), NormFactory.withEqualWeights(Norm.MANHATTAN),
                NormFactory.withEqualWeights(Norm.EUCLIDEAN));
        MechanismFactory large = new VariableNormCCGFactory(new VCGReferencePointFactory(), NormFactory.withEqualWeights(Norm.MANHATTAN), new NormFactory(Norm.MANHATTAN,
                new PayoffWeightsFactory()));
        MechanismFactory small = new VariableNormCCGFactory(new VCGReferencePointFactory(), NormFactory.withEqualWeights(Norm.MANHATTAN), new NormFactory(Norm.MANHATTAN,
                new InversePayoffWeightsFactory()));
        MechanismFactory fractional = new VariableNormCCGFactory(new VCGReferencePointFactory(), NormFactory.withEqualWeights(Norm.MANHATTAN), new NormFactory(Norm.EUCLIDEAN,
                new InversePayoffWeightsFactory()));
        System.out.println(quadratic.getOutcomeRule(bids).getPayment());
        System.out.println(fractional.getOutcomeRule(bids).getPayment());
        System.out.println(small.getOutcomeRule(bids).getPayment());
        System.out.println(large.getOutcomeRule(bids).getPayment());
        System.out.println(new ORVCGRule(bids).getPayment());

    }
}
