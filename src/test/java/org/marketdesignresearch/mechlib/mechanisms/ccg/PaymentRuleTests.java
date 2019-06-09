package org.marketdesignresearch.mechlib.mechanisms.ccg;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.marketdesignresearch.mechlib.domain.*;
import org.marketdesignresearch.mechlib.domain.bid.Bids;
import org.marketdesignresearch.mechlib.domain.bidder.value.BundleValue;
import org.marketdesignresearch.mechlib.domain.bidder.XORBidder;
import org.marketdesignresearch.mechlib.domain.bidder.value.XORValue;
import org.marketdesignresearch.mechlib.mechanisms.AuctionMechanism;
import org.marketdesignresearch.mechlib.mechanisms.ccg.paymentrules.*;
import org.marketdesignresearch.mechlib.mechanisms.ccg.referencepoint.BidsReferencePointFactory;
import org.marketdesignresearch.mechlib.mechanisms.ccg.referencepoint.VCGReferencePointFactory;
import org.marketdesignresearch.mechlib.mechanisms.vcg.ORVCGAuction;
import org.marketdesignresearch.mechlib.utils.CPLEXUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class PaymentRuleTests {
    @Test
    public void testEqualRule() {
        CPLEXUtils.SOLVER.initializeSolveParams();
        SimpleGood west = new SimpleGood("west");
        SimpleGood east = new SimpleGood("east");

        BundleValue westBundle = new BundleValue(BigDecimal.valueOf(1), ImmutableSet.of(west), "west");
        XORBidder westBidder = new XORBidder("west", new XORValue(ImmutableSet.of(westBundle)));

        BundleValue eastBundle = new BundleValue(BigDecimal.valueOf(2.5), ImmutableSet.of(east), "east");
        XORBidder eastBidder = new XORBidder("east", new XORValue(ImmutableSet.of(eastBundle)));

        BundleValue globalBundle = new BundleValue(BigDecimal.valueOf(2), ImmutableSet.of(west, east), "global");
        XORBidder globalBidder = new XORBidder("global", new XORValue(ImmutableSet.of(globalBundle)));

        SimpleXORDomain domain = new SimpleXORDomain(ImmutableList.of(westBidder, eastBidder, globalBidder), ImmutableList.of(west, east));
        MechanismFactory equalNorm = new VariableNormCCGFactory(new BidsReferencePointFactory(), new NormFactory(Norm.MANHATTAN, new EqualWeightsFactory(), Payment.ZERO),
                NormFactory.withEqualWeights(Norm.EUCLIDEAN));
        AuctionMechanism mechanism = equalNorm.getMechanism(Bids.fromXORBidders(domain.getBidders()));
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
        Bids bids = Bids.fromXORBidders(SimpleXORDomain.fromCatsFile(Paths.get("src/test/resources/supersimple.txt")).getBidders());
        MechanismFactory quadratic = new VariableNormCCGFactory(new VCGReferencePointFactory(), NormFactory.withEqualWeights(Norm.MANHATTAN),
                NormFactory.withEqualWeights(Norm.EUCLIDEAN));
        MechanismFactory large = new VariableNormCCGFactory(new VCGReferencePointFactory(), NormFactory.withEqualWeights(Norm.MANHATTAN), new NormFactory(Norm.MANHATTAN,
                new PayoffWeightsFactory()));
        MechanismFactory small = new VariableNormCCGFactory(new VCGReferencePointFactory(), NormFactory.withEqualWeights(Norm.MANHATTAN), new NormFactory(Norm.MANHATTAN,
                new InversePayoffWeightsFactory()));
        MechanismFactory fractional = new VariableNormCCGFactory(new VCGReferencePointFactory(), NormFactory.withEqualWeights(Norm.MANHATTAN), new NormFactory(Norm.EUCLIDEAN,
                new InversePayoffWeightsFactory()));
        System.out.println(quadratic.getMechanism(bids).getPayment());
        System.out.println(fractional.getMechanism(bids).getPayment());
        System.out.println(small.getMechanism(bids).getPayment());
        System.out.println(large.getMechanism(bids).getPayment());
        System.out.println(new ORVCGAuction(bids).getPayment());

    }
}
