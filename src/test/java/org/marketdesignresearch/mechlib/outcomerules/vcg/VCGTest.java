package org.marketdesignresearch.mechlib.outcomerules.vcg;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.Payment;
import org.marketdesignresearch.mechlib.core.SimpleGood;
import org.marketdesignresearch.mechlib.core.SimpleXORDomain;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValuePair;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.bidder.XORBidder;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.SimpleBidInteraction;
import org.marketdesignresearch.mechlib.mechanism.auctions.simple.SimpleBidAuction;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRule;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRuleGenerator;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class VCGTest {

    private SimpleGood A;
    private SimpleGood B;
    private SimpleGood C;
    private SimpleGood D;

    @Before
    public void setUp() {
        A = new SimpleGood("0");
        B = new SimpleGood("1");
        C = new SimpleGood("2");
        D = new SimpleGood("3");

    }

    @Test
    public void testSimpleORWinnerDetermination() {
        BundleExactValuePair bid1 = new BundleExactValuePair(BigDecimal.valueOf(2), Sets.newHashSet(A), "1");
        BundleExactValuePair bid2 = new BundleExactValuePair(BigDecimal.valueOf(3), Sets.newHashSet(A, B, D), "2");
        BundleExactValuePair bid3 = new BundleExactValuePair(BigDecimal.valueOf(2), Sets.newHashSet(B, C), "3");
        BundleExactValuePair bid4 = new BundleExactValuePair(BigDecimal.valueOf(1), Sets.newHashSet(C, D), "4");
        BundleExactValueBids bids = new BundleExactValueBids();
        Bidder bidder1 = new XORBidder("B" + 1);
        Bidder bidder2 = new XORBidder("B" + 2);
        Bidder bidder3 = new XORBidder("B" + 3);
        Bidder bidder4 = new XORBidder("B" + 4);
        bids.setBid(bidder1, new BundleExactValueBid(Sets.newHashSet(bid1)));
        bids.setBid(bidder2, new BundleExactValueBid(Sets.newHashSet(bid2)));
        bids.setBid(bidder3, new BundleExactValueBid(Sets.newHashSet(bid3)));
        bids.setBid(bidder4, new BundleExactValueBid(Sets.newHashSet(bid4)));
        OutcomeRule am = new ORVCGRule(bids);
        Payment payment = am.getPayment();
        assertThat(am.getAllocation().getTotalAllocationValue().doubleValue()).isEqualTo(4);
        assertThat(payment.paymentOf(bidder1).getAmount().doubleValue()).isEqualTo(1);
        assertThat(payment.paymentOf(bidder2).getAmount()).isZero();
        assertThat(payment.paymentOf(bidder3).getAmount().doubleValue()).isEqualTo(1);
        assertThat(payment.paymentOf(bidder4).getAmount()).isZero();
    }

    @Test
    public void testSimpleXORWinnerDetermination() {
        BundleExactValuePair bid1 = new BundleExactValuePair(BigDecimal.valueOf(2), Sets.newHashSet(A), "1");
        BundleExactValuePair bid2 = new BundleExactValuePair(BigDecimal.valueOf(3), Sets.newHashSet(A, B, D), "2");
        BundleExactValuePair bid3 = new BundleExactValuePair(BigDecimal.valueOf(2), Sets.newHashSet(B, C), "3");
        BundleExactValuePair bid4 = new BundleExactValuePair(BigDecimal.valueOf(1), Sets.newHashSet(C, D), "4");
        BundleExactValueBids bids = new BundleExactValueBids();
        Bidder bidder1 = new XORBidder("B" + 1);
        Bidder bidder2 = new XORBidder("B" + 2);
        Bidder bidder3 = new XORBidder("B" + 3);
        Bidder bidder4 = new XORBidder("B" + 4);
        bids.setBid(bidder1, new BundleExactValueBid(Sets.newHashSet(bid1)));
        bids.setBid(bidder2, new BundleExactValueBid(Sets.newHashSet(bid2)));
        bids.setBid(bidder3, new BundleExactValueBid(Sets.newHashSet(bid3)));
        bids.setBid(bidder4, new BundleExactValueBid(Sets.newHashSet(bid4)));
        OutcomeRule am = new XORVCGRule(bids);
        Payment payment = am.getPayment();
        assertThat(am.getAllocation().getTotalAllocationValue().doubleValue()).isEqualTo(4);
        assertThat(payment.paymentOf(bidder1).getAmount().doubleValue()).isEqualTo(1);
        assertThat(payment.paymentOf(bidder2).getAmount()).isZero();
        assertThat(payment.paymentOf(bidder3).getAmount().doubleValue()).isEqualTo(1);
        assertThat(payment.paymentOf(bidder4).getAmount()).isZero();
    }

    @Test
    public void testAuctionWrapper() {
        XORBidder bidder1 = new XORBidder("B" + 1);
        XORBidder bidder2 = new XORBidder("B" + 2);
        XORBidder bidder3 = new XORBidder("B" + 3);
        XORBidder bidder4 = new XORBidder("B" + 4);

        SimpleXORDomain domain = new SimpleXORDomain(Lists.newArrayList(bidder1, bidder2, bidder3, bidder4), Lists.newArrayList(A, B, C, D));
        SimpleBidAuction auction = new SimpleBidAuction(domain, OutcomeRuleGenerator.VCG_XOR);

        BundleExactValuePair bid1 = new BundleExactValuePair(BigDecimal.valueOf(2), Sets.newHashSet(A), "1");
        BundleExactValuePair bid2 = new BundleExactValuePair(BigDecimal.valueOf(3), Sets.newHashSet(A, B, D), "2");
        BundleExactValuePair bid3 = new BundleExactValuePair(BigDecimal.valueOf(2), Sets.newHashSet(B, C), "3");
        BundleExactValuePair bid4 = new BundleExactValuePair(BigDecimal.valueOf(1), Sets.newHashSet(C, D), "4");
        ((SimpleBidInteraction)auction.getCurrentInteraction(bidder1)).submitBid(new BundleExactValueBid(Sets.newHashSet(bid1)));
        ((SimpleBidInteraction)auction.getCurrentInteraction(bidder2)).submitBid(new BundleExactValueBid(Sets.newHashSet(bid2)));
        ((SimpleBidInteraction)auction.getCurrentInteraction(bidder3)).submitBid(new BundleExactValueBid(Sets.newHashSet(bid3)));
        ((SimpleBidInteraction)auction.getCurrentInteraction(bidder4)).submitBid(new BundleExactValueBid(Sets.newHashSet(bid4)));

        auction.closeRound();

        Allocation allocation = auction.getAllocation();
        Payment payment = auction.getPayment();

        assertThat(allocation.getTotalAllocationValue().doubleValue()).isEqualTo(4);
        assertThat(payment.paymentOf(domain.getBidder("B" + 1)).getAmount().doubleValue()).isEqualTo(1);
        assertThat(payment.paymentOf(domain.getBidder("B" + 2)).getAmount()).isZero();
        assertThat(payment.paymentOf(domain.getBidder("B" + 3)).getAmount()).isOne();
        assertThat(payment.paymentOf(domain.getBidder("B" + 4)).getAmount()).isZero();
    }

    @Test
    public void testAuctionWrapperSingleGood() {
        XORBidder bidder1 = new XORBidder("B" + 1);
        XORBidder bidder2 = new XORBidder("B" + 2);
        XORBidder bidder3 = new XORBidder("B" + 3);

        SimpleXORDomain domain = new SimpleXORDomain(Lists.newArrayList(bidder1, bidder2, bidder3), Lists.newArrayList(A));
        SimpleBidAuction auction = new SimpleBidAuction(domain, OutcomeRuleGenerator.VCG_XOR);

        BundleExactValuePair bid1 = new BundleExactValuePair(BigDecimal.valueOf(10), Sets.newHashSet(A), "1");
        BundleExactValuePair bid2 = new BundleExactValuePair(BigDecimal.valueOf(20), Sets.newHashSet(A), "2");
        BundleExactValuePair bid3 = new BundleExactValuePair(BigDecimal.valueOf(30), Sets.newHashSet(A), "3");
        ((SimpleBidInteraction)auction.getCurrentInteraction(bidder1)).submitBid(new BundleExactValueBid(Sets.newHashSet(bid1)));
        ((SimpleBidInteraction)auction.getCurrentInteraction(bidder2)).submitBid(new BundleExactValueBid(Sets.newHashSet(bid2)));
        ((SimpleBidInteraction)auction.getCurrentInteraction(bidder3)).submitBid(new BundleExactValueBid(Sets.newHashSet(bid3)));
        auction.closeRound();
        
        Allocation allocation = auction.getAllocation();
        Payment payment = auction.getPayment();

        assertThat(allocation.getTotalAllocationValue().doubleValue()).isEqualTo(30);
        assertThat(payment.paymentOf(bidder1).getAmount()).isZero();
        assertThat(payment.paymentOf(bidder2).getAmount()).isZero();
        assertThat(payment.paymentOf(bidder3).getAmount().doubleValue()).isEqualTo(20);
    }

}
