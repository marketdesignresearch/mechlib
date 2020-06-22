package org.marketdesignresearch.mechlib.outcomerules.itemlevel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.math.BigDecimal;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.Payment;
import org.marketdesignresearch.mechlib.core.SimpleGood;
import org.marketdesignresearch.mechlib.core.SimpleXORDomain;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValuePair;
import org.marketdesignresearch.mechlib.core.bid.bundle.SingleItemBids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.bidder.XORBidder;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.SimpleBidInteraction;
import org.marketdesignresearch.mechlib.mechanism.auctions.simple.SimpleBidAuction;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRuleGenerator;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class SecondPriceRuleTest {

    private SimpleGood item;
    private XORBidder bidder1;
    private XORBidder bidder2;
    private XORBidder bidder3;

    @Before
    public void setUp() {
        item = new SimpleGood("item");
        bidder1 = new XORBidder("B" + 1);
        bidder2 = new XORBidder("B" + 2);
        bidder3 = new XORBidder("B" + 3);
    }

    @Test
    public void testSimpleSecondPriceAuction() {
        BundleExactValuePair bid1 = new BundleExactValuePair(BigDecimal.valueOf(7), Sets.newHashSet(item), "1");
        BundleExactValuePair bid2 = new BundleExactValuePair(BigDecimal.valueOf(10), Sets.newHashSet(item), "2");
        BundleExactValuePair bid3 = new BundleExactValuePair(BigDecimal.valueOf(8), Sets.newHashSet(item), "3");
        BundleExactValueBids bids = new BundleExactValueBids();
        bids.setBid(bidder1, new BundleExactValueBid(Sets.newHashSet(bid1)));
        bids.setBid(bidder2, new BundleExactValueBid(Sets.newHashSet(bid2)));
        bids.setBid(bidder3, new BundleExactValueBid(Sets.newHashSet(bid3)));
        SingleItemBids singleItemBids = new SingleItemBids(bids);
        Outcome outcome = new SecondPriceRule(singleItemBids).getOutcome();
        checkResult(outcome, bidder2, bid2, BigDecimal.valueOf(8));
    }

    @Test
    public void testSimpleSecondPriceAuctionWithWrapper() {
        SimpleXORDomain domain = new SimpleXORDomain(Lists.newArrayList(bidder1, bidder2, bidder3), Lists.newArrayList(item));
        SimpleBidAuction auction = new SimpleBidAuction(domain, OutcomeRuleGenerator.SECOND_PRICE);

        BundleExactValuePair bid1 = new BundleExactValuePair(BigDecimal.valueOf(7), Sets.newHashSet(item), "1");
        BundleExactValuePair bid2 = new BundleExactValuePair(BigDecimal.valueOf(10), Sets.newHashSet(item), "2");
        BundleExactValuePair bid3 = new BundleExactValuePair(BigDecimal.valueOf(8), Sets.newHashSet(item), "3");
        ((SimpleBidInteraction)auction.getCurrentInteraction(bidder1)).submitBid(new BundleExactValueBid(Sets.newHashSet(bid1)));
        ((SimpleBidInteraction)auction.getCurrentInteraction(bidder2)).submitBid(new BundleExactValueBid(Sets.newHashSet(bid2)));
        ((SimpleBidInteraction)auction.getCurrentInteraction(bidder3)).submitBid(new BundleExactValueBid(Sets.newHashSet(bid3)));

        auction.closeRound();

        Outcome outcome = auction.getOutcome();
        checkResult(outcome, bidder2, bid2, BigDecimal.valueOf(8));
    }

    @Test
    public void testSecondPriceAuctionTwoWinningBids() {
        BundleExactValuePair bid1 = new BundleExactValuePair(BigDecimal.valueOf(10), Sets.newHashSet(item), "1");
        BundleExactValuePair bid2 = new BundleExactValuePair(BigDecimal.valueOf(10), Sets.newHashSet(item), "2");
        BundleExactValuePair bid3 = new BundleExactValuePair(BigDecimal.valueOf(8), Sets.newHashSet(item), "3");
        BundleExactValueBids bids = new BundleExactValueBids();
        bids.setBid(bidder1, new BundleExactValueBid(Sets.newHashSet(bid1)));
        bids.setBid(bidder2, new BundleExactValueBid(Sets.newHashSet(bid2)));
        bids.setBid(bidder3, new BundleExactValueBid(Sets.newHashSet(bid3)));
        SingleItemBids singleItemBids = new SingleItemBids(bids);
        Outcome outcome = new SecondPriceRule(singleItemBids).getOutcome();
        checkResult(outcome, bidder1, bid1, BigDecimal.TEN);
    }

    @Test
    public void testSecondPriceAuctionNoBidder() {
        SingleItemBids bids = new SingleItemBids(new BundleExactValueBids());
        Outcome outcome = new SecondPriceRule(bids).getOutcome();
        Allocation allocation = outcome.getAllocation();
        assertThat(allocation.getTotalAllocationValue()).isZero();
        Payment payment = outcome.getPayment();
        assertThat(payment.getTotalPayments()).isZero();
        assertThat(payment.getPaymentMap()).isEmpty();
    }

    @Test
    public void testSecondPriceAuctionSingleBidder() {
        BundleExactValuePair bid = new BundleExactValuePair(BigDecimal.valueOf(10), Sets.newHashSet(item), "1");

        BundleExactValueBids bids = new BundleExactValueBids();
        bids.setBid(bidder1, new BundleExactValueBid(Sets.newHashSet(bid)));
        SingleItemBids singleItemBids = new SingleItemBids(bids);
        Outcome outcome = new SecondPriceRule(singleItemBids).getOutcome();
        checkResult(outcome, bidder1, bid, BigDecimal.ZERO); // FIXME: Had accepted bid
    }

    @Test
    public void testInvalidSingleGoodAuction() {
        BundleExactValuePair bid1 = new BundleExactValuePair(BigDecimal.valueOf(2), Sets.newHashSet(item), "1");
        BundleExactValuePair bid2 = new BundleExactValuePair(BigDecimal.valueOf(10), Sets.newHashSet(new SimpleGood("item2")), "2");
        BundleExactValuePair bid3 = new BundleExactValuePair(BigDecimal.valueOf(15), Sets.newHashSet(item, new SimpleGood("item3", 2, false)), "3");

        // FIXME
        //assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> bid3.getBundle().getSingleGood());
        //assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> new Bundle(ImmutableMap.of(item, 3)).getSingleGood());

        BundleExactValueBids bidsOnTwoDifferentGoods = new BundleExactValueBids();
        bidsOnTwoDifferentGoods.setBid(bidder1, new BundleExactValueBid(Sets.newHashSet(bid1)));
        bidsOnTwoDifferentGoods.setBid(bidder2, new BundleExactValueBid(Sets.newHashSet(bid2)));
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> new SingleItemBids(bidsOnTwoDifferentGoods));

        BundleExactValueBids bidsOnComplexGood = new BundleExactValueBids();
        bidsOnComplexGood.setBid(bidder1, new BundleExactValueBid(Sets.newHashSet(bid1)));
        bidsOnComplexGood.setBid(bidder3, new BundleExactValueBid(Sets.newHashSet(bid3)));
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> new SingleItemBids(bidsOnComplexGood));
    }

    private void checkResult(Outcome outcome, Bidder expectedWinner, BundleExactValuePair expectedWinningBid, BigDecimal expectedPayment) {
        Set<Bidder> losers = Sets.newHashSet(bidder1, bidder2, bidder3);
        losers.remove(expectedWinner);
        Allocation allocation = outcome.getAllocation();
        Payment payment = outcome.getPayment();
        assertThat(allocation.getTotalAllocationValue()).isEqualTo(expectedWinningBid.getAmount());
        for (Bidder bidder : losers) {
            assertThat(allocation.allocationOf(bidder).getAcceptedBids()).isEmpty();
            assertThat(allocation.allocationOf(bidder).getValue()).isZero();
            assertThat(allocation.allocationOf(bidder).getBundle().getBundleEntries()).isEmpty();
            assertThat(payment.paymentOf(bidder).getAmount()).isZero();
        }

        assertThat(allocation.allocationOf(expectedWinner).getAcceptedBids()).hasSize(1);
        BundleExactValuePair winningBid = allocation.allocationOf(expectedWinner).getAcceptedBids().iterator().next();
        assertThat(winningBid.getId()).isEqualTo(expectedWinningBid.getId());
        assertThat(winningBid.getAmount()).isEqualTo(expectedWinningBid.getAmount());
        assertThat(winningBid.getBundle().getSingleGood()).isEqualTo(item);
        assertThat(allocation.allocationOf(expectedWinner).getBundle().getBundleEntries()).hasSize(1);
        assertThat(allocation.allocationOf(expectedWinner).getBundle().getBundleEntries().iterator().next().getGood()).isEqualTo(item);
        assertThat(allocation.allocationOf(expectedWinner).getValue()).isEqualTo(BigDecimal.TEN);

        assertThat(payment.getTotalPayments()).isEqualTo(expectedPayment);
        assertThat(payment.paymentOf(expectedWinner).getAmount()).isEqualTo(expectedPayment);

    }

}
