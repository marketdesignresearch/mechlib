package org.marketdesignresearch.mechlib.mechanism.auctions.cca;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.marketdesignresearch.mechlib.core.*;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.bidder.ORBidder;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.BundleValue;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.ORValueFunction;
import org.marketdesignresearch.mechlib.input.cats.CATSAdapter;
import org.marketdesignresearch.mechlib.input.cats.CATSAuction;
import org.marketdesignresearch.mechlib.input.cats.CATSParser;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;
import org.marketdesignresearch.mechlib.core.bid.demand.DemandBid;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRuleGenerator;
import org.marketdesignresearch.mechlib.mechanism.auctions.cca.priceupdate.PriceUpdater;
import org.marketdesignresearch.mechlib.mechanism.auctions.cca.priceupdate.SimpleRelativePriceUpdate;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.DemandQuery;
import org.marketdesignresearch.mechlib.mechanism.auctions.cca.bidcollection.supplementaryround.ProfitMaximizingSupplementaryRound;
import org.marketdesignresearch.mechlib.outcomerules.vcg.VCGRule;
import org.marketdesignresearch.mechlib.outcomerules.vcg.XORVCGRule;
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
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.Assert.assertEquals;

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
        cca.addSupplementaryRound(new ProfitMaximizingSupplementaryRound().withNumberOfSupplementaryBids(3));
        Outcome outcome = cca.getOutcome();
        assertThat(outcome.getAllocation().getTotalAllocationValue().doubleValue()).isEqualTo(8240.2519, Offset.offset(1e-4));
        log.info(outcome.toString());
    }

    @Test
    public void testCCAWithCATSAuctionAndVCG() {
        CCAuction cca = new CCAuction(domain, OutcomeRuleGenerator.VCG_XOR);
        PriceUpdater priceUpdater = new SimpleRelativePriceUpdate().withInitialUpdate(BigDecimal.TEN);
        cca.setPriceUpdater(priceUpdater);
        cca.addSupplementaryRound(new ProfitMaximizingSupplementaryRound().withNumberOfSupplementaryBids(3));
        Outcome outcome = cca.getOutcome();
        assertThat(outcome.getAllocation().getTotalAllocationValue().doubleValue()).isEqualTo(8240.2519, Offset.offset(1e-4));
        log.info(outcome.toString());
    }

    @Test
    public void testRoundAfterRoundCCAWithCATSAuction() {
        VCGRule auction = new XORVCGRule(BundleValueBids.fromXORBidders(domain.getBidders()));
        Outcome resultIncludingAllBids = auction.getOutcome();

        CCAuction cca = new CCAuction(domain);
        PriceUpdater priceUpdater = new SimpleRelativePriceUpdate().withInitialUpdate(BigDecimal.TEN);
        cca.setPriceUpdater(priceUpdater);
        cca.addSupplementaryRound(new ProfitMaximizingSupplementaryRound().withNumberOfSupplementaryBids(2));
        cca.addSupplementaryRound(new ProfitMaximizingSupplementaryRound().withNumberOfSupplementaryBids(3));
        Allocation previousAllocation = Allocation.EMPTY_ALLOCATION;
        while (!cca.finished()) {
            cca.advanceRound();
            Allocation allocation = new XORWinnerDetermination(cca.getLatestAggregatedBids()).getAllocation();
            assertThat(allocation.getTotalAllocationValue()).isLessThanOrEqualTo(resultIncludingAllBids.getAllocation().getTotalAllocationValue());
            assertThat(allocation.getTotalAllocationValue()).isGreaterThanOrEqualTo(previousAllocation.getTotalAllocationValue());
            previousAllocation = allocation;
        }
        Outcome outcome = cca.getOutcome();
        assertThat(outcome.getAllocation().getTotalAllocationValue().doubleValue()).isEqualTo(8240.2519, Offset.offset(1e-4));
        assertThat(outcome.getAllocation()).isEqualTo(previousAllocation);
        log.info(outcome.toString());
    }

    @Test
    public void testFinishPhase() {
        VCGRule auction = new XORVCGRule(BundleValueBids.fromXORBidders(domain.getBidders()));
        Outcome resultIncludingAllBids = auction.getOutcome();

        CCAuction cca = new CCAuction(domain);
        PriceUpdater priceUpdater = new SimpleRelativePriceUpdate().withInitialUpdate(BigDecimal.TEN);
        cca.setPriceUpdater(priceUpdater);
        cca.addSupplementaryRound(new ProfitMaximizingSupplementaryRound().withNumberOfSupplementaryBids(2));
        cca.addSupplementaryRound(new ProfitMaximizingSupplementaryRound().withNumberOfSupplementaryBids(3));
        Allocation previousAllocation = Allocation.EMPTY_ALLOCATION;
        while (!cca.currentPhaseFinished()) {
            cca.advanceRound();
            Allocation allocation = new XORWinnerDetermination(cca.getLatestAggregatedBids()).getAllocation();
            assertThat(allocation.getTotalAllocationValue()).isLessThanOrEqualTo(resultIncludingAllBids.getAllocation().getTotalAllocationValue());
            assertThat(allocation.getTotalAllocationValue()).isGreaterThanOrEqualTo(previousAllocation.getTotalAllocationValue());
            previousAllocation = allocation;
        }
        assertThat(cca.isClockPhaseCompleted()).isTrue();
        assertThat(cca.hasNextSupplementaryRound()).isTrue();
        while (!cca.finished()) {
            cca.advanceRound();
            Allocation allocation = new XORWinnerDetermination(cca.getLatestAggregatedBids()).getAllocation();
            assertThat(allocation.getTotalAllocationValue()).isLessThanOrEqualTo(resultIncludingAllBids.getAllocation().getTotalAllocationValue());
            assertThat(allocation.getTotalAllocationValue()).isGreaterThanOrEqualTo(previousAllocation.getTotalAllocationValue());
            previousAllocation = allocation;
        }
        assertThat(cca.isClockPhaseCompleted()).isTrue();
        assertThat(cca.hasNextSupplementaryRound()).isFalse();
        Outcome outcome = cca.getOutcome();
        assertThat(outcome.getAllocation().getTotalAllocationValue().doubleValue()).isEqualTo(8240.2519, Offset.offset(1e-4));
        assertThat(outcome.getAllocation()).isEqualTo(previousAllocation);
        log.info(outcome.toString());
    }

    @Test
    public void testResettingCCAWithCATSAuction() {
        CCAuction cca = new CCAuction(domain);
        PriceUpdater priceUpdater = new SimpleRelativePriceUpdate().withInitialUpdate(BigDecimal.TEN);
        cca.setPriceUpdater(priceUpdater);
        cca.addSupplementaryRound(new ProfitMaximizingSupplementaryRound().withNumberOfSupplementaryBids(2));
        cca.addSupplementaryRound(new ProfitMaximizingSupplementaryRound().withNumberOfSupplementaryBids(3));
        cca.addSupplementaryRound(new ProfitMaximizingSupplementaryRound().withNumberOfSupplementaryBids(4));
        cca.addSupplementaryRound(new ProfitMaximizingSupplementaryRound().withNumberOfSupplementaryBids(3));
        Outcome first = cca.getOutcome();
        assertThat(cca.isClockPhaseCompleted()).isTrue();
        assertThat(cca.hasNextSupplementaryRound()).isFalse();
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> cca.resetToRound(50));
        cca.resetToRound(25);
        assertThat(cca.isClockPhaseCompleted()).isFalse();
        assertThat(cca.hasNextSupplementaryRound()).isTrue();

        VCGRule auction = new XORVCGRule(cca.getLatestAggregatedBids());
        Outcome intermediate = auction.getOutcome();
        assertThat(intermediate.getAllocation().getTotalAllocationValue())
                .isLessThan(first.getAllocation().getTotalAllocationValue());

        Outcome second = cca.getOutcome();
        assertThat(cca.isClockPhaseCompleted()).isTrue();
        assertThat(cca.hasNextSupplementaryRound()).isFalse();
        assertThat(second.getAllocation().getTotalAllocationValue())
                .isEqualTo(first.getAllocation().getTotalAllocationValue());

    }

    @Test
    public void testProposeStartingPrices() {
        SimpleGood goodA = new SimpleGood("A");
        SimpleGood goodB = new SimpleGood("B");
        Bundle A = Bundle.of(Sets.newHashSet(goodA));
        Bundle B = Bundle.of(Sets.newHashSet(goodB));
        Bundle AB = Bundle.of(Sets.newHashSet(goodA, goodB));

        Set<BundleValue> value1 = new HashSet<>();
        value1.add(new BundleValue(BigDecimal.valueOf(28), A));
        value1.add(new BundleValue(BigDecimal.valueOf(16), B));
        Set<BundleValue> value2 = new HashSet<>();
        value2.add(new BundleValue(BigDecimal.valueOf(7), A));
        value2.add(new BundleValue(BigDecimal.valueOf(28), B));
        Set<BundleValue> value3 = new HashSet<>();
        value3.add(new BundleValue(BigDecimal.valueOf(14), A));
        value3.add(new BundleValue(BigDecimal.valueOf(14), B));
        ORBidder bidder1 = new ORBidder("1", new ORValueFunction(value1));
        ORBidder bidder2 = new ORBidder("2", new ORValueFunction(value2));
        ORBidder bidder3 = new ORBidder("3", new ORValueFunction(value3));
        Domain domain = new SimpleORDomain(Lists.newArrayList(bidder1, bidder2, bidder3), Lists.newArrayList(goodA, goodB));
        CCAuction cca = new CCAuction(domain, OutcomeRuleGenerator.VCG_XOR, true);
        cca.setPriceUpdater(new SimpleRelativePriceUpdate().withInitialUpdate(BigDecimal.ONE).withPriceUpdate(BigDecimal.valueOf(2)));
        cca.addSupplementaryRound(new ProfitMaximizingSupplementaryRound().withNumberOfSupplementaryBids(3));
        // TODO
        //assertThat(cca.restrictedBids()).isEmpty();
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
        Bundle A = Bundle.of(Sets.newHashSet(goodA));
        Bundle B = Bundle.of(Sets.newHashSet(goodB));
        Bundle AB = Bundle.of(Sets.newHashSet(goodA, goodB));

        Set<BundleValue> value1 = new HashSet<>();
        value1.add(new BundleValue(BigDecimal.valueOf(28), A));
        value1.add(new BundleValue(BigDecimal.valueOf(2), B));
        Set<BundleValue> value2 = new HashSet<>();
        value2.add(new BundleValue(BigDecimal.valueOf(2), A));
        value2.add(new BundleValue(BigDecimal.valueOf(28), B));
        Set<BundleValue> value3 = new HashSet<>();
        value3.add(new BundleValue(BigDecimal.valueOf(10), A));
        value3.add(new BundleValue(BigDecimal.valueOf(2), B));
        ORBidder bidder1 = new ORBidder("1", new ORValueFunction(value1));
        ORBidder bidder2 = new ORBidder("2", new ORValueFunction(value2));
        ORBidder bidder3 = new ORBidder("3", new ORValueFunction(value3));
        Domain domain = new SimpleORDomain(Lists.newArrayList(bidder1, bidder2, bidder3), Lists.newArrayList(goodA, goodB));

        CCAuction cca = new CCAuction(domain, OutcomeRuleGenerator.VCG_XOR, false);
        cca.setPriceUpdater(new SimpleRelativePriceUpdate().withInitialUpdate(BigDecimal.ONE).withPriceUpdate(BigDecimal.valueOf(2)));
        cca.addSupplementaryRound(new ProfitMaximizingSupplementaryRound().withNumberOfSupplementaryBids(3));
        // TODO
        //assertThat(cca.restrictedBids()).isEmpty();
        assertThat(cca.allowedNumberOfBids()).isOne();
        assertThat(cca.hasNextSupplementaryRound()).isTrue();
        assertThat(cca.isClockPhaseCompleted()).isFalse();
        assertThat(cca.getCurrentPrices().getPrice(A).getAmount()).isZero();
        assertThat(cca.getCurrentPrices().getPrice(B).getAmount()).isZero();
        assertThat(cca.getCurrentPrices().getPrice(AB).getAmount()).isZero();


        // First round
        BundleValueBids<BundleValuePair> bids = new BundleValueBids<BundleValuePair>();
        for (Bidder bidder : domain.getBidders()) {
            Bundle bestBundle = bidder.getBestBundle(cca.getCurrentPrices());
            BundleValuePair bundleBid = new BundleValuePair(cca.getCurrentPrices().getPrice(bestBundle).getAmount(), bestBundle, UUID.randomUUID().toString());
            BundleValueBid<BundleValuePair> bid = new BundleValueBid<>(Sets.newHashSet(bundleBid));
            DemandQuery query = (DemandQuery) cca.getCurrentInteraction(bidder);
            assertEquals(bestBundle, query.proposeDemandBid().getDemandedBundle());
            bids.setBid(bidder, bid);
        }
        // Set all bids
        Outcome temp = cca.getTemporaryResult();
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
        assertThat(cca.getOutcomeAtRound(0)).isEqualTo(temp);

        // Second round
        for (Bidder bidder : domain.getBidders()) {
            Bundle bestBundle = bidder.getBestBundle(cca.getCurrentPrices());
            BundleValuePair bundleBid = new BundleValuePair(cca.getCurrentPrices().getPrice(bestBundle).getAmount(), bestBundle, UUID.randomUUID().toString());
            BundleValueBid<BundleValuePair> bid = new BundleValueBid<>(Sets.newHashSet(bundleBid));
            DemandQuery query = (DemandQuery) cca.getCurrentInteraction(bidder);
            assertEquals(bestBundle, query.proposeDemandBid().getDemandedBundle());
            // Submit bids one by one
            query.submitDemandBid(new DemandBid(bestBundle));
        }

        temp = cca.getTemporaryResult();

        assertThat(temp.getAllocation().getTotalAllocationValue()).isEqualTo(BigDecimal.valueOf(2));
        assertThat(temp.getPayment().getTotalPayments()).isEqualTo(BigDecimal.valueOf(2));

        cca.closeRound();
        assertThat(cca.getCurrentPrices().getPrice(A).getAmount()).isEqualTo(BigDecimal.valueOf(3));
        assertThat(cca.getCurrentPrices().getPrice(B).getAmount()).isEqualTo(BigDecimal.valueOf(3));
        assertThat(cca.getCurrentPrices().getPrice(AB).getAmount()).isEqualTo(BigDecimal.valueOf(6));
        assertThat(cca.getOutcomeAtRound(1)).isEqualTo(temp);

        // Third round
        Bundle bestBundle = bidder2.getBestBundle(cca.getCurrentPrices());
        BundleValuePair bundleBid = new BundleValuePair(cca.getCurrentPrices().getPrice(bestBundle).getAmount(), bestBundle, UUID.randomUUID().toString());
        BundleValueBid<BundleValuePair> bid = new BundleValueBid<>(Sets.newHashSet(bundleBid));
        DemandQuery query = (DemandQuery) cca.getCurrentInteraction(bidder2);
        assertEquals(bestBundle,query.proposeDemandBid().getDemandedBundle());
        query.submitDemandBid(new DemandBid(bestBundle));

        bestBundle = bidder3.getBestBundle(cca.getCurrentPrices());
        bundleBid = new BundleValuePair(cca.getCurrentPrices().getPrice(bestBundle).getAmount(), bestBundle, UUID.randomUUID().toString());
        bid = new BundleValueBid<>(Sets.newHashSet(bundleBid));
        query = (DemandQuery) cca.getCurrentInteraction(bidder3);
        assertEquals(bestBundle, query.proposeDemandBid().getDemandedBundle());
        query.submitDemandBid(new DemandBid(bestBundle));

        // Workaround for known bug in java compilation if using it directly this way:
        // assertThat(cca.getTemporaryResult().getWinners()).containsExactlyInAnyOrder(bidder2, bidder3);
        assertThat(cca.getTemporaryResult().getWinners()).hasSize(2);
        assertThat(cca.getTemporaryResult().getWinners().contains(bidder2)).isTrue();
        assertThat(cca.getTemporaryResult().getWinners().contains(bidder3)).isTrue();

        bestBundle = bidder1.getBestBundle(cca.getCurrentPrices());
        bundleBid = new BundleValuePair(cca.getCurrentPrices().getPrice(bestBundle).getAmount(), bestBundle, UUID.randomUUID().toString());
        bid = new BundleValueBid<>(Sets.newHashSet(bundleBid));
        query = (DemandQuery) cca.getCurrentInteraction(bidder1);
        assertEquals(bestBundle, query.proposeDemandBid().getDemandedBundle());
        query.submitDemandBid(new DemandBid(bestBundle));

        temp = cca.getTemporaryResult();
        assertThat(temp.getAllocation().getTotalAllocationValue()).isEqualTo(BigDecimal.valueOf(6));
        assertThat(temp.getPayment().getTotalPayments()).isEqualTo(BigDecimal.valueOf(3));

        cca.closeRound();
        assertThat(cca.getCurrentPrices().getPrice(A).getAmount()).isEqualTo(BigDecimal.valueOf(9));
        assertThat(cca.getCurrentPrices().getPrice(B).getAmount()).isEqualTo(BigDecimal.valueOf(3)); // Was not over-demanded
        assertThat(cca.getCurrentPrices().getPrice(AB).getAmount()).isEqualTo(BigDecimal.valueOf(12));
        assertThat(cca.getOutcomeAtRound(2)).isEqualTo(temp);

        // Fourth round
        for (Bidder bidder : domain.getBidders()) {
            Bundle bundle = bidder.getBestBundle(cca.getCurrentPrices());
            BundleValuePair bb = new BundleValuePair(cca.getCurrentPrices().getPrice(bundle).getAmount(), bundle, UUID.randomUUID().toString());
            bid = new BundleValueBid<>(Sets.newHashSet(bb));
            query = (DemandQuery) cca.getCurrentInteraction(bidder);
            assertEquals(bestBundle, query.proposeDemandBid().getDemandedBundle());
            query.submitDemandBid(new DemandBid(bestBundle));
        }

        temp = cca.getTemporaryResult();
        assertThat(temp.getAllocation().getTotalAllocationValue()).isEqualTo(BigDecimal.valueOf(12));
        assertThat(temp.getPayment().getTotalPayments()).isEqualTo(BigDecimal.valueOf(9));

        cca.closeRound();
        assertThat(cca.getCurrentPrices().getPrice(A).getAmount()).isEqualTo(BigDecimal.valueOf(27));
        assertThat(cca.getCurrentPrices().getPrice(B).getAmount()).isEqualTo(BigDecimal.valueOf(3)); // Was not over-demanded
        assertThat(cca.getCurrentPrices().getPrice(AB).getAmount()).isEqualTo(BigDecimal.valueOf(30));
        assertThat(cca.getOutcomeAtRound(3)).isEqualTo(temp);


        // Fifth round
        for (Bidder bidder : domain.getBidders()) {
            Bundle bundle = bidder.getBestBundle(cca.getCurrentPrices());
            BundleValuePair bb = new BundleValuePair(cca.getCurrentPrices().getPrice(bundle).getAmount(), bundle, UUID.randomUUID().toString());
            bid = new BundleValueBid<>(Sets.newHashSet(bb));
            query = (DemandQuery) cca.getCurrentInteraction(bidder);
            assertEquals(bestBundle, query.proposeDemandBid().getDemandedBundle());
            query.submitDemandBid(new DemandBid(bestBundle));
        }

        temp = cca.getTemporaryResult();
        assertThat(temp.getAllocation().getTotalAllocationValue()).isEqualTo(BigDecimal.valueOf(30));
        assertThat(temp.getPayment().getTotalPayments()).isEqualTo(BigDecimal.valueOf(0));

        cca.closeRound();
        // No change anymore, no over-demand
        assertThat(cca.getCurrentPrices().getPrice(A).getAmount()).isEqualTo(BigDecimal.valueOf(27));
        assertThat(cca.getCurrentPrices().getPrice(B).getAmount()).isEqualTo(BigDecimal.valueOf(3));
        assertThat(cca.getCurrentPrices().getPrice(AB).getAmount()).isEqualTo(BigDecimal.valueOf(30));

        assertThat(cca.isClockPhaseCompleted()).isTrue();
        Outcome aggregated = cca.getOutcomeRuleGenerator().getOutcomeRule(cca.getLatestAggregatedBids()).getOutcome();

        // Workaround for known bug in java compilation if using it directly this way:
        // assertThat(cca.getTemporaryResult().getWinners()).containsExactlyInAnyOrder(bidder2, bidder3);
        assertThat(aggregated.getWinners()).hasSize(2);
        assertThat(aggregated.getWinners().contains(bidder1)).isTrue();
        assertThat(aggregated.getWinners().contains(bidder2)).isTrue();

        assertThat(aggregated.getAllocation().allocationOf(bidder1).getBundle()).isEqualTo(A);
        assertThat(aggregated.getAllocation().allocationOf(bidder1).getValue()).isEqualTo(BigDecimal.valueOf(27));
        assertThat(aggregated.getAllocation().allocationOf(bidder2).getBundle()).isEqualTo(B);
        assertThat(aggregated.getAllocation().allocationOf(bidder2).getValue()).isEqualTo(BigDecimal.valueOf(3));
        assertThat(aggregated.getPayment().paymentOf(bidder1).getAmount()).isEqualTo(BigDecimal.valueOf(9));
        assertThat(aggregated.getPayment().paymentOf(bidder2)).isEqualTo(BidderPayment.ZERO_PAYMENT);
        assertThat(aggregated.getPayment().paymentOf(bidder3)).isEqualTo(BidderPayment.ZERO_PAYMENT);

        // TODO: Test supplementary round as well
    }

    private void checkBidEquality(BundleValueBid<BundleValuePair> bid, BundleValueBid<BundleValuePair> proposedBid) {
        assertThat(bid.getBundleBids().size()).isOne();
        assertThat(proposedBid.getBundleBids().size()).isOne();
        assertThat(bid.getBundleBids().iterator().next().getBundle()).isEqualTo(proposedBid.getBundleBids().iterator().next().getBundle());
        assertThat(bid.getBundleBids().iterator().next().getAmount()).isEqualTo(proposedBid.getBundleBids().iterator().next().getAmount());
    }

}
