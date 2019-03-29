package ch.uzh.ifi.ce.mechanisms.ccg;

import ch.uzh.ifi.ce.domain.*;
import ch.uzh.ifi.ce.domain.cats.Domain;
import ch.uzh.ifi.ce.mechanisms.AuctionMechanism;
import ch.uzh.ifi.ce.mechanisms.ccg.paymentrules.*;
import ch.uzh.ifi.ce.mechanisms.ccg.referencepoint.BidsReferencePointFactory;
import ch.uzh.ifi.ce.mechanisms.ccg.referencepoint.VCGReferencePointFactory;
import ch.uzh.ifi.ce.mechanisms.vcg.ORVCGAuction;
import ch.uzh.ifi.ce.utils.CPLEXUtils;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class PaymentRuleTests {
    @Test
    public void testEqualRule() {
        CPLEXUtils.SOLVER.initializeSolveParams();
        Bidder westBidder = new Bidder("west");
        Bidder eastBidder = new Bidder("east");
        Bidder globalBidder = new Bidder("global");
        Good west = new SimpleGood("west");
        Good east = new SimpleGood("east");

        BundleValue westBundle = new BundleValue(BigDecimal.valueOf(1), ImmutableSet.of(west), "west");
        Value valueWest = new Value(ImmutableSet.of(westBundle), ValueType.LOCAL_WEST);
        BundleValue eastBundle = new BundleValue(BigDecimal.valueOf(2.5), ImmutableSet.of(east), "east");
        Value valueEast = new Value(ImmutableSet.of(eastBundle), ValueType.LOCAL_EAST);
        BundleValue globalBundle = new BundleValue(BigDecimal.valueOf(2), ImmutableSet.of(west, east), "global");
        Value globalValue = new Value(ImmutableSet.of(globalBundle), ValueType.GLOBAL);
        Map<Bidder, Value> valueMap = ImmutableMap.of(westBidder, valueWest, eastBidder, valueEast, globalBidder, globalValue);
        Domain domain = new Domain(new Values(valueMap), ImmutableSet.of(west, east), null);
        MechanismFactory equalNorm = new VariableNormCCGFactory(new BidsReferencePointFactory(), new NormFactory(Norm.MANHATTAN, new EqualWeightsFactory(), Payment.ZERO),
                NormFactory.withEqualWeights(Norm.EUCLIDEAN));
        AuctionMechanism mechanism = equalNorm.getMechanism(domain.toAuction());
        Payment payment = mechanism.getPayment();
        assertThat(payment.paymentOf(eastBidder).getAmount()).isEqualByComparingTo(BigDecimal.valueOf(1.75));
        assertThat(payment.paymentOf(westBidder).getAmount()).isEqualByComparingTo(BigDecimal.valueOf(.25));

    }

    @Test
    public void testLubinParkesNames() {
        ParameterizableMechanismFactory mechFactory = new VariableNormCCGFactory(new BidsReferencePointFactory(), new NormFactory(Norm.MANHATTAN, new EqualWeightsFactory()),
                NormFactory.withEqualWeights(Norm.EUCLIDEAN));
        System.out.println(mechFactory.lubinParkesName());
    }

    @Test
    public void paperExample() throws IOException {
        CPLEXUtils.SOLVER.initializeSolveParams();
        AuctionInstance auctionInstance = Domain.fromCatsFile(Paths.get("src/test/resources/supersimple.txt")).toAuction();
        MechanismFactory quadratic = new VariableNormCCGFactory(new VCGReferencePointFactory(), NormFactory.withEqualWeights(Norm.MANHATTAN),
                NormFactory.withEqualWeights(Norm.EUCLIDEAN));
        MechanismFactory large = new VariableNormCCGFactory(new VCGReferencePointFactory(), NormFactory.withEqualWeights(Norm.MANHATTAN), new NormFactory(Norm.MANHATTAN,
                new PayoffWeightsFactory()));
        MechanismFactory small = new VariableNormCCGFactory(new VCGReferencePointFactory(), NormFactory.withEqualWeights(Norm.MANHATTAN), new NormFactory(Norm.MANHATTAN,
                new InversePayoffWeightsFactory()));
        MechanismFactory fractional = new VariableNormCCGFactory(new VCGReferencePointFactory(), NormFactory.withEqualWeights(Norm.MANHATTAN), new NormFactory(Norm.EUCLIDEAN,
                new InversePayoffWeightsFactory()));
        System.out.println(quadratic.getMechanism(auctionInstance).getPayment());
        System.out.println(fractional.getMechanism(auctionInstance).getPayment());
        System.out.println(small.getMechanism(auctionInstance).getPayment());
        System.out.println(large.getMechanism(auctionInstance).getPayment());
        System.out.println(new ORVCGAuction(auctionInstance).getPayment());

    }
}
