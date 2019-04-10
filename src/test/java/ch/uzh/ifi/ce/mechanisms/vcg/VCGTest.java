package ch.uzh.ifi.ce.mechanisms.vcg;

import ch.uzh.ifi.ce.domain.*;
import ch.uzh.ifi.ce.domain.bidder.SimpleBidder;
import ch.uzh.ifi.ce.mechanisms.AuctionMechanism;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;

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
        bids.setBid(new SimpleBidder("B" + 1), new Bid(Sets.newHashSet(bid1)));
        bids.setBid(new SimpleBidder("B" + 2), new Bid(Sets.newHashSet(bid2)));
        bids.setBid(new SimpleBidder("B" + 3), new Bid(Sets.newHashSet(bid3)));
        bids.setBid(new SimpleBidder("B" + 4), new Bid(Sets.newHashSet(bid4)));
        AuctionInstance auctionInstance = new AuctionInstance(bids);
        AuctionMechanism am = new ORVCGAuction(auctionInstance);
        Payment payment = am.getPayment();
        assertThat(am.getAllocation().getTotalAllocationValue().doubleValue()).isEqualTo(4);
        assertThat(payment.paymentOf(new SimpleBidder("B" + 1)).getAmount().doubleValue()).isEqualTo(1);
        assertThat(payment.paymentOf(new SimpleBidder("B" + 2)).getAmount()).isZero();
        assertThat(payment.paymentOf(new SimpleBidder("B" + 3)).getAmount().doubleValue()).isEqualTo(1);
        assertThat(payment.paymentOf(new SimpleBidder("B" + 4)).getAmount()).isZero();
    }

    @Test
    public void testSimpleXORWinnerDetermination() {
        BundleBid bid1 = new BundleBid(BigDecimal.valueOf(2), Sets.newHashSet(A), "1");
        BundleBid bid2 = new BundleBid(BigDecimal.valueOf(3), Sets.newHashSet(A, B, D), "2");
        BundleBid bid3 = new BundleBid(BigDecimal.valueOf(2), Sets.newHashSet(B, C), "3");
        BundleBid bid4 = new BundleBid(BigDecimal.valueOf(1), Sets.newHashSet(C, D), "4");
        Bids bids = new Bids();
        bids.setBid(new SimpleBidder("B" + 1), new Bid(Sets.newHashSet(bid1)));
        bids.setBid(new SimpleBidder("B" + 2), new Bid(Sets.newHashSet(bid2)));
        bids.setBid(new SimpleBidder("B" + 3), new Bid(Sets.newHashSet(bid3)));
        bids.setBid(new SimpleBidder("B" + 4), new Bid(Sets.newHashSet(bid4)));
        AuctionInstance auctionInstance = new AuctionInstance(bids);
        AuctionMechanism am = new XORVCGAuction(auctionInstance);
        Payment payment = am.getPayment();
        assertThat(am.getAllocation().getTotalAllocationValue().doubleValue()).isEqualTo(4);
        assertThat(payment.paymentOf(new SimpleBidder("B" + 1)).getAmount().doubleValue()).isEqualTo(1);
        assertThat(payment.paymentOf(new SimpleBidder("B" + 2)).getAmount()).isZero();
        assertThat(payment.paymentOf(new SimpleBidder("B" + 3)).getAmount().doubleValue()).isEqualTo(1);
        assertThat(payment.paymentOf(new SimpleBidder("B" + 4)).getAmount()).isZero();
    }

}
