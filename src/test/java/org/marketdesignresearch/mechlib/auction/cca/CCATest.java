package org.marketdesignresearch.mechlib.auction.cca;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.marketdesignresearch.mechlib.domain.*;
import org.marketdesignresearch.mechlib.domain.bid.Bid;
import org.marketdesignresearch.mechlib.domain.bid.Bids;
import org.marketdesignresearch.mechlib.domain.bidder.Bidder;
import org.marketdesignresearch.mechlib.domain.bidder.ORBidder;
import org.marketdesignresearch.mechlib.domain.bidder.XORBidder;
import org.marketdesignresearch.mechlib.domain.bidder.value.BundleValue;
import org.marketdesignresearch.mechlib.domain.bidder.value.ORValue;
import org.marketdesignresearch.mechlib.domain.bidder.value.XORValue;
import org.marketdesignresearch.mechlib.domain.cats.CATSAdapter;
import org.marketdesignresearch.mechlib.domain.cats.CATSAuction;
import org.marketdesignresearch.mechlib.domain.cats.CATSParser;
import org.marketdesignresearch.mechlib.mechanisms.MechanismResult;
import org.marketdesignresearch.mechlib.mechanisms.MechanismType;
import org.marketdesignresearch.mechlib.auction.cca.priceupdate.PriceUpdater;
import org.marketdesignresearch.mechlib.auction.cca.priceupdate.SimpleRelativePriceUpdate;
import org.marketdesignresearch.mechlib.auction.cca.bidcollection.supplementaryround.ProfitMaximizingSupplementaryRound;
import org.marketdesignresearch.mechlib.mechanisms.vcg.VCGMechanism;
import org.marketdesignresearch.mechlib.mechanisms.vcg.XORVCGMechanism;
import org.marketdesignresearch.mechlib.winnerdetermination.XORWinnerDetermination;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.data.Offset;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@Slf4j
public class CCATest {

    private static SimpleXORDomain domain;

    @BeforeClass
    public static void setUp() throws IOException {
        Path catsFile = Paths.get("src/test/resources/hard0000.txt");
        CATSParser parser = new CATSParser();
        CATSAuction catsAuction = parser.readCatsAuctionBean(catsFile);
        CATSAdapter adapter = new CATSAdapter();
        domain = adapter.adaptToDomain(catsAuction);
    }

    @Test
    public void testCCAWithCATSAuction() {
        CCAuction cca = new CCAuction(domain);
        PriceUpdater priceUpdater = new SimpleRelativePriceUpdate().withInitialUpdate(BigDecimal.TEN);
        cca.setPriceUpdater(priceUpdater);
        cca.addSupplementaryRound(new ProfitMaximizingSupplementaryRound(cca).withNumberOfSupplementaryBids(3));
        MechanismResult mechanismResult = cca.getMechanismResult();
        assertThat(mechanismResult.getAllocation().getTotalAllocationValue().doubleValue()).isEqualTo(8240.2519, Offset.offset(1e-4));
        log.info(mechanismResult.toString());
    }

    @Test
    public void testCCAWithCATSAuctionAndVCG() {
        CCAuction cca = new CCAuction(domain, MechanismType.VCG_XOR);
        PriceUpdater priceUpdater = new SimpleRelativePriceUpdate().withInitialUpdate(BigDecimal.TEN);
        cca.setPriceUpdater(priceUpdater);
        cca.addSupplementaryRound(new ProfitMaximizingSupplementaryRound(cca).withNumberOfSupplementaryBids(3));
        MechanismResult mechanismResult = cca.getMechanismResult();
        assertThat(mechanismResult.getAllocation().getTotalAllocationValue().doubleValue()).isEqualTo(8240.2519, Offset.offset(1e-4));
        log.info(mechanismResult.toString());
    }

    @Test
    public void testRoundAfterRoundCCAWithCATSAuction() {
        VCGMechanism auction = new XORVCGMechanism(Bids.fromXORBidders(domain.getBidders()));
        MechanismResult resultIncludingAllBids = auction.getMechanismResult();

        CCAuction cca = new CCAuction(domain);
        PriceUpdater priceUpdater = new SimpleRelativePriceUpdate().withInitialUpdate(BigDecimal.TEN);
        cca.setPriceUpdater(priceUpdater);
        cca.addSupplementaryRound(new ProfitMaximizingSupplementaryRound(cca).withNumberOfSupplementaryBids(2));
        cca.addSupplementaryRound(new ProfitMaximizingSupplementaryRound(cca).withNumberOfSupplementaryBids(3));
        cca.addSupplementaryRound(new ProfitMaximizingSupplementaryRound(cca).withNumberOfSupplementaryBids(4));
        Allocation previousAllocation = Allocation.EMPTY_ALLOCATION;
        while (!cca.isClockPhaseCompleted()) {
            cca.nextClockRound();
            Allocation allocation = new XORWinnerDetermination(cca.getLatestAggregatedBids()).getAllocation();
            assertThat(allocation.getTotalAllocationValue()).isLessThanOrEqualTo(resultIncludingAllBids.getAllocation().getTotalAllocationValue());
            assertThat(allocation.getTotalAllocationValue()).isGreaterThanOrEqualTo(previousAllocation.getTotalAllocationValue());
            previousAllocation = allocation;
        }
        while (cca.hasNextSupplementaryRound()) {
            cca.nextSupplementaryRound();
            Allocation allocation = new XORWinnerDetermination(cca.getLatestAggregatedBids()).getAllocation();
            assertThat(allocation.getTotalAllocationValue()).isLessThanOrEqualTo(resultIncludingAllBids.getAllocation().getTotalAllocationValue());
            assertThat(allocation.getTotalAllocationValue()).isGreaterThanOrEqualTo(previousAllocation.getTotalAllocationValue());
            previousAllocation = allocation;
        }
        MechanismResult mechanismResult = cca.getMechanismResult();
        assertThat(mechanismResult.getAllocation().getTotalAllocationValue().doubleValue()).isEqualTo(8240.2519, Offset.offset(1e-4));
        assertThat(mechanismResult.getAllocation()).isEqualTo(previousAllocation);
        log.info(mechanismResult.toString());
    }

    @Test
    public void testResettingCCAWithCATSAuction() {
        CCAuction cca = new CCAuction(domain);
        PriceUpdater priceUpdater = new SimpleRelativePriceUpdate().withInitialUpdate(BigDecimal.TEN);
        cca.setPriceUpdater(priceUpdater);
        cca.addSupplementaryRound(new ProfitMaximizingSupplementaryRound(cca).withNumberOfSupplementaryBids(2));
        cca.addSupplementaryRound(new ProfitMaximizingSupplementaryRound(cca).withNumberOfSupplementaryBids(3));
        cca.addSupplementaryRound(new ProfitMaximizingSupplementaryRound(cca).withNumberOfSupplementaryBids(4));
        cca.addSupplementaryRound(new ProfitMaximizingSupplementaryRound(cca).withNumberOfSupplementaryBids(3));
        MechanismResult first = cca.getMechanismResult();
        assertThat(cca.isClockPhaseCompleted()).isTrue();
        assertThat(cca.hasNextSupplementaryRound()).isFalse();
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> cca.resetToRound(50));
        cca.resetToRound(25);
        assertThat(cca.isClockPhaseCompleted()).isFalse();
        assertThat(cca.hasNextSupplementaryRound()).isTrue();

        VCGMechanism auction = new XORVCGMechanism(cca.getLatestAggregatedBids());
        MechanismResult intermediate = auction.getMechanismResult();
        assertThat(intermediate.getAllocation().getTotalAllocationValue())
                .isLessThan(first.getAllocation().getTotalAllocationValue());

        MechanismResult second = cca.getMechanismResult();
        assertThat(cca.isClockPhaseCompleted()).isTrue();
        assertThat(cca.hasNextSupplementaryRound()).isFalse();
        assertThat(second.getAllocation().getTotalAllocationValue())
                .isEqualTo(first.getAllocation().getTotalAllocationValue());

    }

    @Test
    public void testProposeStartingPrices() {
        SimpleGood goodA = new SimpleGood("A");
        SimpleGood goodB = new SimpleGood("B");
        Bundle A = Bundle.singleGoods(Sets.newHashSet(goodA));
        Bundle B = Bundle.singleGoods(Sets.newHashSet(goodB));
        Bundle AB = Bundle.singleGoods(Sets.newHashSet(goodA, goodB));

        ORValue value1 = new ORValue();
        value1.addBundleValue(new BundleValue(BigDecimal.valueOf(28), A));
        value1.addBundleValue(new BundleValue(BigDecimal.valueOf(16), B));
        ORValue value2 = new ORValue();
        value2.addBundleValue(new BundleValue(BigDecimal.valueOf(7), A));
        value2.addBundleValue(new BundleValue(BigDecimal.valueOf(28), B));
        ORValue value3 = new ORValue();
        value3.addBundleValue(new BundleValue(BigDecimal.valueOf(14), A));
        value3.addBundleValue(new BundleValue(BigDecimal.valueOf(14), B));
        ORBidder bidder1 = new ORBidder("1", value1);
        ORBidder bidder2 = new ORBidder("2", value2);
        ORBidder bidder3 = new ORBidder("3", value3);
        Domain domain = new SimpleORDomain(Lists.newArrayList(bidder1, bidder2, bidder3), Lists.newArrayList(goodA, goodB));
        CCAuction cca = new CCAuction(domain, MechanismType.VCG_XOR);
        cca.setPriceUpdater(new SimpleRelativePriceUpdate().withInitialUpdate(BigDecimal.ONE).withPriceUpdate(BigDecimal.valueOf(2)));
        cca.addSupplementaryRound(new ProfitMaximizingSupplementaryRound(cca).withNumberOfSupplementaryBids(3));
        assertThat(cca.nextGoods()).isEqualTo(domain.getGoods());
        assertThat(cca.allowedNumberOfBids()).isOne();
        assertThat(cca.hasNextSupplementaryRound()).isTrue();
        assertThat(cca.isClockPhaseCompleted()).isFalse();
        assertThat(cca.getCurrentPrices().getPrice(A).getAmount()).isEqualTo(BigDecimal.valueOf(1.63));
        assertThat(cca.getCurrentPrices().getPrice(B).getAmount()).isEqualTo(BigDecimal.valueOf(1.93));
        assertThat(cca.getCurrentPrices().getPrice(AB).getAmount()).isEqualTo(BigDecimal.valueOf(3.56));
    }

    @Test
    public void testStepByStepCCA() {
        SimpleGood goodA = new SimpleGood("A");
        SimpleGood goodB = new SimpleGood("B");
        Bundle A = Bundle.singleGoods(Sets.newHashSet(goodA));
        Bundle B = Bundle.singleGoods(Sets.newHashSet(goodB));
        Bundle AB = Bundle.singleGoods(Sets.newHashSet(goodA, goodB));

        ORValue value1 = new ORValue();
        value1.addBundleValue(new BundleValue(BigDecimal.valueOf(28), A));
        value1.addBundleValue(new BundleValue(BigDecimal.valueOf(2), B));
        ORValue value2 = new ORValue();
        value2.addBundleValue(new BundleValue(BigDecimal.valueOf(2), A));
        value2.addBundleValue(new BundleValue(BigDecimal.valueOf(28), B));
        ORValue value3 = new ORValue();
        value3.addBundleValue(new BundleValue(BigDecimal.valueOf(10), A));
        value3.addBundleValue(new BundleValue(BigDecimal.valueOf(2), B));
        ORBidder bidder1 = new ORBidder("1", value1);
        ORBidder bidder2 = new ORBidder("2", value2);
        ORBidder bidder3 = new ORBidder("3", value3);
        Domain domain = new SimpleORDomain(Lists.newArrayList(bidder1, bidder2, bidder3), Lists.newArrayList(goodA, goodB));
        CCAuction cca = new CCAuction(domain, MechanismType.VCG_XOR, false);
        cca.setPriceUpdater(new SimpleRelativePriceUpdate().withInitialUpdate(BigDecimal.ONE).withPriceUpdate(BigDecimal.valueOf(2)));
        cca.addSupplementaryRound(new ProfitMaximizingSupplementaryRound(cca).withNumberOfSupplementaryBids(3));
        assertThat(cca.nextGoods()).isEqualTo(domain.getGoods());
        assertThat(cca.allowedNumberOfBids()).isOne();
        assertThat(cca.hasNextSupplementaryRound()).isTrue();
        assertThat(cca.isClockPhaseCompleted()).isFalse();
        assertThat(cca.getCurrentPrices().getPrice(A).getAmount()).isZero();
        assertThat(cca.getCurrentPrices().getPrice(B).getAmount()).isZero();
        assertThat(cca.getCurrentPrices().getPrice(AB).getAmount()).isZero();


        // First round
        Bids bids = new Bids();
        for (Bidder bidder : domain.getBidders()) {
            Bundle bestBundle = bidder.getBestBundle(cca.getCurrentPrices());
            BundleBid bundleBid = new BundleBid(cca.getCurrentPrices().getPrice(bestBundle).getAmount(), bestBundle, UUID.randomUUID().toString());
            bids.setBid(bidder, new Bid(Sets.newHashSet(bundleBid)));
        }
        // Set all bids
        cca.submitBids(bids);
        MechanismResult temp = cca.getTemporaryResult();
        assertThat(temp.getAllocation().getTotalAllocationValue()).isZero();
        assertThat(temp.getAllocation().allocationOf(bidder1).getValue()).isZero();
        assertThat(temp.getAllocation().allocationOf(bidder2).getValue()).isZero();
        assertThat(temp.getAllocation().allocationOf(bidder3).getValue()).isZero();
        assertThat(temp.getPayment().getTotalPayments()).isZero();
        assertThat(temp.getPayment().paymentOf(bidder1)).isEqualTo(BidderPayment.ZERO_PAYMENT);
        assertThat(temp.getPayment().paymentOf(bidder2)).isEqualTo(BidderPayment.ZERO_PAYMENT);
        assertThat(temp.getPayment().paymentOf(bidder3)).isEqualTo(BidderPayment.ZERO_PAYMENT);

        cca.closeRound();
        assertThat(cca.getCurrentPrices().getPrice(A).getAmount()).isOne();
        assertThat(cca.getCurrentPrices().getPrice(B).getAmount()).isOne();
        assertThat(cca.getCurrentPrices().getPrice(AB).getAmount()).isEqualTo(BigDecimal.valueOf(2));
        assertThat(cca.getAuctionResultAtRound(0)).isEqualTo(temp);

        // Second round
        for (Bidder bidder : domain.getBidders()) {
            Bundle bestBundle = bidder.getBestBundle(cca.getCurrentPrices());
            BundleBid bundleBid = new BundleBid(cca.getCurrentPrices().getPrice(bestBundle).getAmount(), bestBundle, UUID.randomUUID().toString());
            // Submit bids one by one
            cca.submitBid(bidder, new Bid(Sets.newHashSet(bundleBid)));
        }

        temp = cca.getTemporaryResult();

        assertThat(temp.getAllocation().getTotalAllocationValue()).isEqualTo(BigDecimal.valueOf(2));
        assertThat(temp.getPayment().getTotalPayments()).isEqualTo(BigDecimal.valueOf(2));

        cca.closeRound();
        assertThat(cca.getCurrentPrices().getPrice(A).getAmount()).isEqualTo(BigDecimal.valueOf(3));
        assertThat(cca.getCurrentPrices().getPrice(B).getAmount()).isEqualTo(BigDecimal.valueOf(3));
        assertThat(cca.getCurrentPrices().getPrice(AB).getAmount()).isEqualTo(BigDecimal.valueOf(6));
        assertThat(cca.getAuctionResultAtRound(1)).isEqualTo(temp);

        // Third round
        Bundle bestBundle = bidder2.getBestBundle(cca.getCurrentPrices());
        BundleBid bundleBid = new BundleBid(cca.getCurrentPrices().getPrice(bestBundle).getAmount(), bestBundle, UUID.randomUUID().toString());
        cca.submitBid(bidder2, new Bid(Sets.newHashSet(bundleBid)));

        bestBundle = bidder3.getBestBundle(cca.getCurrentPrices());
        bundleBid = new BundleBid(cca.getCurrentPrices().getPrice(bestBundle).getAmount(), bestBundle, UUID.randomUUID().toString());
        cca.submitBid(bidder3, new Bid(Sets.newHashSet(bundleBid)));

        assertThat(cca.getTemporaryResult().getWinners()).containsExactlyInAnyOrder(bidder2, bidder3);

        bestBundle = bidder1.getBestBundle(cca.getCurrentPrices());
        bundleBid = new BundleBid(cca.getCurrentPrices().getPrice(bestBundle).getAmount().add(BigDecimal.ONE), bestBundle, UUID.randomUUID().toString());
        cca.submitBid(bidder1, new Bid(Sets.newHashSet(bundleBid)));

        temp = cca.getTemporaryResult();
        assertThat(temp.getWinners()).containsExactlyInAnyOrder(bidder1, bidder2);
        assertThat(temp.getAllocation().getTotalAllocationValue()).isEqualTo(BigDecimal.valueOf(7));
        assertThat(temp.getPayment().getTotalPayments()).isEqualTo(BigDecimal.valueOf(3));

        cca.closeRound();
        assertThat(cca.getCurrentPrices().getPrice(A).getAmount()).isEqualTo(BigDecimal.valueOf(9));
        assertThat(cca.getCurrentPrices().getPrice(B).getAmount()).isEqualTo(BigDecimal.valueOf(3)); // Was not over-demanded
        assertThat(cca.getCurrentPrices().getPrice(AB).getAmount()).isEqualTo(BigDecimal.valueOf(12));
        assertThat(cca.getAuctionResultAtRound(2)).isEqualTo(temp);

        // Fourth round
        for (Bidder bidder : domain.getBidders()) {
            Bundle bundle = bidder.getBestBundle(cca.getCurrentPrices());
            BundleBid bid = new BundleBid(cca.getCurrentPrices().getPrice(bundle).getAmount(), bundle, UUID.randomUUID().toString());
            cca.submitBid(bidder, new Bid(Sets.newHashSet(bid)));
        }

        temp = cca.getTemporaryResult();
        assertThat(temp.getAllocation().getTotalAllocationValue()).isEqualTo(BigDecimal.valueOf(12));
        assertThat(temp.getPayment().getTotalPayments()).isEqualTo(BigDecimal.valueOf(9));

        cca.closeRound();
        assertThat(cca.getCurrentPrices().getPrice(A).getAmount()).isEqualTo(BigDecimal.valueOf(27));
        assertThat(cca.getCurrentPrices().getPrice(B).getAmount()).isEqualTo(BigDecimal.valueOf(3)); // Was not over-demanded
        assertThat(cca.getCurrentPrices().getPrice(AB).getAmount()).isEqualTo(BigDecimal.valueOf(30));
        assertThat(cca.getAuctionResultAtRound(3)).isEqualTo(temp);


        // Fifth round
        for (Bidder bidder : domain.getBidders()) {
            Bundle bundle = bidder.getBestBundle(cca.getCurrentPrices());
            BundleBid bid = new BundleBid(cca.getCurrentPrices().getPrice(bundle).getAmount(), bundle, UUID.randomUUID().toString());
            cca.submitBid(bidder, new Bid(Sets.newHashSet(bid)));
        }

        temp = cca.getTemporaryResult();
        assertThat(temp.getAllocation().getTotalAllocationValue()).isEqualTo(BigDecimal.valueOf(30));
        assertThat(temp.getPayment().getTotalPayments()).isEqualTo(BigDecimal.valueOf(0));

        cca.closeRound();
        // No change anymore, no over-demand
        assertThat(cca.getCurrentPrices().getPrice(A).getAmount()).isEqualTo(BigDecimal.valueOf(27));
        assertThat(cca.getCurrentPrices().getPrice(B).getAmount()).isEqualTo(BigDecimal.valueOf(3));
        assertThat(cca.getCurrentPrices().getPrice(AB).getAmount()).isEqualTo(BigDecimal.valueOf(30));
        assertThat(cca.getAuctionResultAtRound(4)).isEqualTo(temp);

        assertThat(cca.isClockPhaseCompleted()).isTrue();
        MechanismResult aggregated = cca.getMechanismType().getMechanism(cca.getLatestAggregatedBids()).getMechanismResult();

        assertThat(aggregated.getWinners()).containsExactlyInAnyOrder(bidder1, bidder2);
        assertThat(aggregated.getAllocation().allocationOf(bidder1).getBundle()).isEqualTo(A);
        assertThat(aggregated.getAllocation().allocationOf(bidder1).getValue()).isEqualTo(BigDecimal.valueOf(27));
        assertThat(aggregated.getAllocation().allocationOf(bidder2).getBundle()).isEqualTo(B);
        assertThat(aggregated.getAllocation().allocationOf(bidder2).getValue()).isEqualTo(BigDecimal.valueOf(3));
        assertThat(aggregated.getPayment().paymentOf(bidder1).getAmount()).isEqualTo(BigDecimal.valueOf(9));
        assertThat(aggregated.getPayment().paymentOf(bidder2)).isEqualTo(BidderPayment.ZERO_PAYMENT);
        assertThat(aggregated.getPayment().paymentOf(bidder3)).isEqualTo(BidderPayment.ZERO_PAYMENT);

        // TODO: Test supplementary round as well
    }


}
