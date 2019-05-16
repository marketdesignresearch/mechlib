package org.marketdesignresearch.mechlib.mechanisms.vcg;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.marketdesignresearch.mechlib.domain.*;
import org.marketdesignresearch.mechlib.domain.auction.Auction;
import org.marketdesignresearch.mechlib.domain.bid.Bid;
import org.marketdesignresearch.mechlib.domain.bid.Bids;
import org.marketdesignresearch.mechlib.domain.bidder.Bidder;
import org.marketdesignresearch.mechlib.domain.bidder.XORBidder;
import org.marketdesignresearch.mechlib.mechanisms.AuctionMechanism;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.marketdesignresearch.mechlib.mechanisms.MechanismType;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

public class VCGTest {

    private Good A;
    private Good B;
    private Good C;
    private Good D;

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
        bids.setBid(new XORBidder("B" + 1), new Bid(Sets.newHashSet(bid1)));
        bids.setBid(new XORBidder("B" + 2), new Bid(Sets.newHashSet(bid2)));
        bids.setBid(new XORBidder("B" + 3), new Bid(Sets.newHashSet(bid3)));
        bids.setBid(new XORBidder("B" + 4), new Bid(Sets.newHashSet(bid4)));
        AuctionMechanism am = new ORVCGAuction(bids);
        Payment payment = am.getPayment();
        assertThat(am.getAllocation().getTotalAllocationValue().doubleValue()).isEqualTo(4);
        assertThat(payment.paymentOf(new XORBidder("B" + 1)).getAmount().doubleValue()).isEqualTo(1);
        assertThat(payment.paymentOf(new XORBidder("B" + 2)).getAmount()).isZero();
        assertThat(payment.paymentOf(new XORBidder("B" + 3)).getAmount().doubleValue()).isEqualTo(1);
        assertThat(payment.paymentOf(new XORBidder("B" + 4)).getAmount()).isZero();
    }

    @Test
    public void testSimpleXORWinnerDetermination() {
        BundleBid bid1 = new BundleBid(BigDecimal.valueOf(2), Sets.newHashSet(A), "1");
        BundleBid bid2 = new BundleBid(BigDecimal.valueOf(3), Sets.newHashSet(A, B, D), "2");
        BundleBid bid3 = new BundleBid(BigDecimal.valueOf(2), Sets.newHashSet(B, C), "3");
        BundleBid bid4 = new BundleBid(BigDecimal.valueOf(1), Sets.newHashSet(C, D), "4");
        Bids bids = new Bids();
        bids.setBid(new XORBidder("B" + 1), new Bid(Sets.newHashSet(bid1)));
        bids.setBid(new XORBidder("B" + 2), new Bid(Sets.newHashSet(bid2)));
        bids.setBid(new XORBidder("B" + 3), new Bid(Sets.newHashSet(bid3)));
        bids.setBid(new XORBidder("B" + 4), new Bid(Sets.newHashSet(bid4)));
        AuctionMechanism am = new XORVCGAuction(bids);
        Payment payment = am.getPayment();
        assertThat(am.getAllocation().getTotalAllocationValue().doubleValue()).isEqualTo(4);
        assertThat(payment.paymentOf(new XORBidder("B" + 1)).getAmount().doubleValue()).isEqualTo(1);
        assertThat(payment.paymentOf(new XORBidder("B" + 2)).getAmount()).isZero();
        assertThat(payment.paymentOf(new XORBidder("B" + 3)).getAmount().doubleValue()).isEqualTo(1);
        assertThat(payment.paymentOf(new XORBidder("B" + 4)).getAmount()).isZero();
    }

    @Test
    public void testAuctionWrapper() throws JsonProcessingException {
        Bidder bidder1 = new XORBidder("B" + 1);
        Bidder bidder2 = new XORBidder("B" + 2);
        Bidder bidder3 = new XORBidder("B" + 3);
        Bidder bidder4 = new XORBidder("B" + 4);

        Domain domain = new Domain(Sets.newHashSet(bidder1, bidder2, bidder3, bidder4), Sets.newHashSet(A, B, C, D));
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

        ObjectMapper mapper = new ObjectMapper();
        String jsonResult = mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(bids);
        System.out.println(jsonResult);

        auction.addRound(bids);

        Allocation allocation = auction.getAllocation();
        Payment payment = auction.getPayment();

        assertThat(allocation.getTotalAllocationValue().doubleValue()).isEqualTo(4);
        assertThat(payment.paymentOf(new XORBidder("B" + 1)).getAmount().doubleValue()).isEqualTo(1);
        assertThat(payment.paymentOf(new XORBidder("B" + 2)).getAmount()).isZero();
        assertThat(payment.paymentOf(new XORBidder("B" + 3)).getAmount().doubleValue()).isEqualTo(1);
        assertThat(payment.paymentOf(new XORBidder("B" + 4)).getAmount()).isZero();
    }

}
