package org.marketdesignresearch.mechlib.outcomerules;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.marketdesignresearch.mechlib.core.*;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.core.bidder.ORBidder;
import org.marketdesignresearch.mechlib.core.bidder.XORBidder;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.BundleValue;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.ORValueFunction;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.XORValueFunction;
import org.marketdesignresearch.mechlib.outcomerules.ccg.MechanismFactory;
import org.marketdesignresearch.mechlib.outcomerules.ccg.VariableAlgorithmCCGFactory;
import org.marketdesignresearch.mechlib.outcomerules.ccg.blockingallocation.OrStarBlockingCoalitionFinderFactory;
import org.marketdesignresearch.mechlib.outcomerules.ccg.blockingallocation.XORBlockingCoalitionFinderFactory;
import org.marketdesignresearch.mechlib.outcomerules.ccg.constraintgeneration.ConstraintGenerationAlgorithm;
import org.marketdesignresearch.mechlib.utils.CPLEXUtils;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class OutcomeRuleScalerTest {

    @Test
    public void testScalerForCCG() {

        CPLEXUtils.SOLVER.initializeSolveParams();
        SimpleGood west = new SimpleGood("west", 2, false);

        BundleValue valueWest1 = new BundleValue(BigDecimal.valueOf(2e15), Bundle.of(west));
        XORBidder westBidder1 = new XORBidder("west1", new XORValueFunction(ImmutableSet.of(valueWest1)));

        BundleValue valueWest2 = new BundleValue(BigDecimal.valueOf(2e15), Bundle.of(west));
        XORBidder westBidder2 = new XORBidder("west2", new XORValueFunction(ImmutableSet.of(valueWest2)));

        BundleValue globalBundle = new BundleValue(BigDecimal.valueOf(3e15), new Bundle(Map.of(west, 2)), "global");
        XORBidder globalBidder = new XORBidder("global", new XORValueFunction(ImmutableSet.of(globalBundle)));

        SimpleXORDomain domain = new SimpleXORDomain(ImmutableList.of(westBidder1, westBidder2, globalBidder), ImmutableList.of(west));
        MechanismFactory equalNorm = new VariableAlgorithmCCGFactory(new XORBlockingCoalitionFinderFactory(), ConstraintGenerationAlgorithm.STANDARD_CCG);
        OutcomeRule outcomeRule = equalNorm.getOutcomeRule(BundleExactValueBids.fromXORBidders(domain.getBidders()));
        Payment payment = outcomeRule.getPayment();
        assertThat(payment.paymentOf(westBidder2).getAmount()).isEqualByComparingTo(BigDecimal.valueOf(1.5e15));
        assertThat(payment.paymentOf(westBidder1).getAmount()).isEqualByComparingTo(BigDecimal.valueOf(1.5e15));
    }
    
    @Test
    public void testScalerForCCGinORDomain() {
        CPLEXUtils.SOLVER.initializeSolveParams();
        SimpleGood west = new SimpleGood("west", 2, false);

        BundleValue valueWest1 = new BundleValue(BigDecimal.valueOf(2e15), Bundle.of(west));
        ORBidder westBidder1 = new ORBidder("west1", new ORValueFunction(ImmutableSet.of(valueWest1)));

        BundleValue valueWest2 = new BundleValue(BigDecimal.valueOf(2e15), Bundle.of(west));
        ORBidder westBidder2 = new ORBidder("west2", new ORValueFunction(ImmutableSet.of(valueWest2)));

        BundleValue globalBundle = new BundleValue(BigDecimal.valueOf(1.5e15), Bundle.of(west), "global");
        ORBidder globalBidder = new ORBidder("global", new ORValueFunction(ImmutableSet.of(globalBundle)));

        SimpleORDomain domain = new SimpleORDomain(ImmutableList.of(westBidder1, westBidder2, globalBidder), ImmutableList.of(west));
        MechanismFactory equalNorm = new VariableAlgorithmCCGFactory(new OrStarBlockingCoalitionFinderFactory(), ConstraintGenerationAlgorithm.STANDARD_CCG);
        OutcomeRule outcomeRule = equalNorm.getOutcomeRule(BundleExactValueBids.fromORBidders(domain.getBidders()));
        Payment payment = outcomeRule.getPayment();
        assertThat(payment.paymentOf(westBidder2).getAmount()).isEqualByComparingTo(BigDecimal.valueOf(1.5e15));
        assertThat(payment.paymentOf(westBidder1).getAmount()).isEqualByComparingTo(BigDecimal.valueOf(1.5e15));
    }
}
