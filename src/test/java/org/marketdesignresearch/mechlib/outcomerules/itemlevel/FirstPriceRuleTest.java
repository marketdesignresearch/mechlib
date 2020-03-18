package org.marketdesignresearch.mechlib.outcomerules.itemlevel;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.marketdesignresearch.mechlib.core.*;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.bidder.XORBidder;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;
import org.marketdesignresearch.mechlib.core.bid.bundle.SingleItemBids;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class FirstPriceRuleTest {

    private Good item;
    private Bidder bidder1;
    private Bidder bidder2;
    private Bidder bidder3;

    @Before
    public void setUp() {
        item = new SimpleGood("item");
        bidder1 = new XORBidder("B" + 1);
        bidder2 = new XORBidder("B" + 2);
        bidder3 = new XORBidder("B" + 3);
    }

    @Test
    public void testSimpleFirstPriceAuction() {
        BundleValuePair bid1A = new BundleValuePair(BigDecimal.valueOf(2), Sets.newHashSet(item), "1A");
        BundleValuePair bid2A = new BundleValuePair(BigDecimal.valueOf(10), Sets.newHashSet(item), "2A");
        BundleValuePair bid3A = new BundleValuePair(BigDecimal.valueOf(3), Sets.newHashSet(item), "3A");
        BundleValueBids<BundleValuePair> bids = new BundleValueBids<>();
        bids.setBid(bidder1, new BundleValueBid<>(Sets.newHashSet(bid1A)));
        bids.setBid(bidder2, new BundleValueBid<>(Sets.newHashSet(bid2A)));
        bids.setBid(bidder3, new BundleValueBid<>(Sets.newHashSet(bid3A)));
        SingleItemBids singleItemBids = new SingleItemBids(bids);
        Outcome outcome = new FirstPriceRule(singleItemBids).getOutcome();
        checkResult(outcome, bidder2, bid2A);
    }

    @Test
    public void testFirstPriceAuctionNoBidder() {
        SingleItemBids bids = new SingleItemBids(new BundleValueBids());
        Outcome outcome = new FirstPriceRule(bids).getOutcome();
        Allocation allocation = outcome.getAllocation();
        assertThat(allocation.getTotalAllocationValue()).isZero();
        Payment payment = outcome.getPayment();
        assertThat(payment.getTotalPayments()).isZero();
        assertThat(payment.getPaymentMap()).isEmpty();
    }

    @Test
    public void testFirstPriceAuctionSingleBidder() {
        BundleValuePair bid1A = new BundleValuePair(BigDecimal.valueOf(10), Sets.newHashSet(item), "1");
        BundleValueBids<BundleValuePair> bids = new BundleValueBids<>();
        bids.setBid(bidder1, new BundleValueBid<>(Sets.newHashSet(bid1A)));
        SingleItemBids singleItemBids = new SingleItemBids(bids);
        Outcome outcome = new FirstPriceRule(singleItemBids).getOutcome();
        checkResult(outcome, bidder1, bid1A);
    }

    private void checkResult(Outcome outcome, Bidder expectedWinner, BundleValuePair expectedWinningBid) {
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
        BundleValuePair winningBid = allocation.allocationOf(expectedWinner).getAcceptedBids().iterator().next();
        assertThat(winningBid.getId()).isEqualTo(expectedWinningBid.getId());
        assertThat(winningBid.getAmount()).isEqualTo(expectedWinningBid.getAmount());
        assertThat(winningBid.getBundle().getSingleGood()).isEqualTo(item);
        assertThat(allocation.allocationOf(expectedWinner).getBundle().getBundleEntries()).hasSize(1);
        assertThat(allocation.allocationOf(expectedWinner).getBundle().getBundleEntries().iterator().next().getGood()).isEqualTo(item);
        assertThat(allocation.allocationOf(expectedWinner).getValue()).isEqualTo(BigDecimal.TEN);

        assertThat(payment.getTotalPayments()).isEqualTo(BigDecimal.TEN);
        assertThat(payment.paymentOf(expectedWinner).getAmount()).isEqualTo(BigDecimal.TEN);

    }

}
