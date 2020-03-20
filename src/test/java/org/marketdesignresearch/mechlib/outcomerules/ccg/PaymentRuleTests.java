package org.marketdesignresearch.mechlib.outcomerules.ccg;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;

import org.junit.Test;
import org.marketdesignresearch.mechlib.core.Payment;
import org.marketdesignresearch.mechlib.core.SimpleGood;
import org.marketdesignresearch.mechlib.core.SimpleXORDomain;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.core.bidder.XORBidder;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.BundleValue;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.XORValueFunction;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRule;
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
    public void testEqualRule() {
        CPLEXUtils.SOLVER.initializeSolveParams();
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
    public void testLubinParkesNames() {
        ParameterizableCCGFactory mechFactory = new VariableNormCCGFactory(new BidsReferencePointFactory(), new NormFactory(Norm.MANHATTAN, new EqualWeightsFactory()),
                NormFactory.withEqualWeights(Norm.EUCLIDEAN));
        System.out.println(mechFactory.lubinParkesName());
    }

    @Test
    public void paperExample() throws IOException {
        CPLEXUtils.SOLVER.initializeSolveParams();
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
