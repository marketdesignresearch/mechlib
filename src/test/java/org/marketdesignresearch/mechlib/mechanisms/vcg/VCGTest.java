package org.marketdesignresearch.mechlib.mechanisms.vcg;

import com.google.common.collect.Lists;
import org.marketdesignresearch.mechlib.core.*;
import org.marketdesignresearch.mechlib.auction.Auction;
import org.marketdesignresearch.mechlib.core.bid.Bid;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.bidder.XORBidder;
import org.marketdesignresearch.mechlib.mechanisms.OutputRule;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.marketdesignresearch.mechlib.mechanisms.MechanismType;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

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
        BundleBid bid1 = new BundleBid(BigDecimal.valueOf(2), Sets.newHashSet(A), "1");
        BundleBid bid2 = new BundleBid(BigDecimal.valueOf(3), Sets.newHashSet(A, B, D), "2");
        BundleBid bid3 = new BundleBid(BigDecimal.valueOf(2), Sets.newHashSet(B, C), "3");
        BundleBid bid4 = new BundleBid(BigDecimal.valueOf(1), Sets.newHashSet(C, D), "4");
        Bids bids = new Bids();
        Bidder bidder1 = new XORBidder("B" + 1);
        Bidder bidder2 = new XORBidder("B" + 2);
        Bidder bidder3 = new XORBidder("B" + 3);
        Bidder bidder4 = new XORBidder("B" + 4);
        bids.setBid(bidder1, new Bid(Sets.newHashSet(bid1)));
        bids.setBid(bidder2, new Bid(Sets.newHashSet(bid2)));
        bids.setBid(bidder3, new Bid(Sets.newHashSet(bid3)));
        bids.setBid(bidder4, new Bid(Sets.newHashSet(bid4)));
        OutputRule am = new ORVCGMechanism(bids);
        Payment payment = am.getPayment();
        assertThat(am.getAllocation().getTotalAllocationValue().doubleValue()).isEqualTo(4);
        assertThat(payment.paymentOf(bidder1).getAmount().doubleValue()).isEqualTo(1);
        assertThat(payment.paymentOf(bidder2).getAmount()).isZero();
        assertThat(payment.paymentOf(bidder3).getAmount().doubleValue()).isEqualTo(1);
        assertThat(payment.paymentOf(bidder4).getAmount()).isZero();
    }

    @Test
    public void testSimpleXORWinnerDetermination() {
        BundleBid bid1 = new BundleBid(BigDecimal.valueOf(2), Sets.newHashSet(A), "1");
        BundleBid bid2 = new BundleBid(BigDecimal.valueOf(3), Sets.newHashSet(A, B, D), "2");
        BundleBid bid3 = new BundleBid(BigDecimal.valueOf(2), Sets.newHashSet(B, C), "3");
        BundleBid bid4 = new BundleBid(BigDecimal.valueOf(1), Sets.newHashSet(C, D), "4");
        Bids bids = new Bids();
        Bidder bidder1 = new XORBidder("B" + 1);
        Bidder bidder2 = new XORBidder("B" + 2);
        Bidder bidder3 = new XORBidder("B" + 3);
        Bidder bidder4 = new XORBidder("B" + 4);
        bids.setBid(bidder1, new Bid(Sets.newHashSet(bid1)));
        bids.setBid(bidder2, new Bid(Sets.newHashSet(bid2)));
        bids.setBid(bidder3, new Bid(Sets.newHashSet(bid3)));
        bids.setBid(bidder4, new Bid(Sets.newHashSet(bid4)));
        OutputRule am = new XORVCGMechanism(bids);
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
        Auction auction = new Auction(domain, MechanismType.VCG_XOR);

        BundleBid bid1 = new BundleBid(BigDecimal.valueOf(2), Sets.newHashSet(A), "1");
        BundleBid bid2 = new BundleBid(BigDecimal.valueOf(3), Sets.newHashSet(A, B, D), "2");
        BundleBid bid3 = new BundleBid(BigDecimal.valueOf(2), Sets.newHashSet(B, C), "3");
        BundleBid bid4 = new BundleBid(BigDecimal.valueOf(1), Sets.newHashSet(C, D), "4");
        Bids bids = new Bids();
        bids.setBid(bidder1, new Bid(Sets.newHashSet(bid1)));
        bids.setBid(bidder2, new Bid(Sets.newHashSet(bid2)));
        bids.setBid(bidder3, new Bid(Sets.newHashSet(bid3)));
        bids.setBid(bidder4, new Bid(Sets.newHashSet(bid4)));

        auction.addRound(bids);

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
        Auction auction = new Auction(domain, MechanismType.VCG_XOR);

        BundleBid bid1 = new BundleBid(BigDecimal.valueOf(10), Sets.newHashSet(A), "1");
        BundleBid bid2 = new BundleBid(BigDecimal.valueOf(20), Sets.newHashSet(A), "2");
        BundleBid bid3 = new BundleBid(BigDecimal.valueOf(30), Sets.newHashSet(A), "3");
        Bids bids = new Bids();
        bids.setBid(bidder1, new Bid(Sets.newHashSet(bid1)));
        bids.setBid(bidder2, new Bid(Sets.newHashSet(bid2)));
        bids.setBid(bidder3, new Bid(Sets.newHashSet(bid3)));

        auction.addRound(bids);

        Allocation allocation = auction.getAllocation();
        Payment payment = auction.getPayment();

        assertThat(allocation.getTotalAllocationValue().doubleValue()).isEqualTo(30);
        assertThat(payment.paymentOf(bidder1).getAmount()).isZero();
        assertThat(payment.paymentOf(bidder2).getAmount()).isZero();
        assertThat(payment.paymentOf(bidder3).getAmount().doubleValue()).isEqualTo(20);
    }

}
