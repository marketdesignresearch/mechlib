package org.marketdesignresearch.mechlib.winnerdetermination;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.marketdesignresearch.mechlib.domain.*;
import org.marketdesignresearch.mechlib.domain.bidder.SimpleBidder;
import org.marketdesignresearch.mechlib.mechanisms.Allocator;
import org.marketdesignresearch.mechlib.mechanisms.AuctionResult;
import org.marketdesignresearch.mechlib.mechanisms.ccg.CCGAuction;
import org.marketdesignresearch.mechlib.mechanisms.ccg.CCGMechanismFactory;
import org.marketdesignresearch.mechlib.mechanisms.ccg.paymentrules.Norm;
import org.marketdesignresearch.mechlib.mechanisms.ccg.paymentrules.VariableNormCCGFactory;
import org.marketdesignresearch.mechlib.mechanisms.ccg.referencepoint.VCGReferencePointFactory;
import org.marketdesignresearch.mechlib.utils.CPLEXUtils;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class WinnerDeterminationTest {
    private AuctionInstance auctionInstance;
    private Good A;
    private Good B;
    private Good C;
    private Good D;
    private Good E;

    @Before
    public void setUp() {
        A = new SimpleGood("0");
        B = new SimpleGood("1");
        C = new SimpleGood("2");
        D = new SimpleGood("3");
        E = new SimpleGood("4");

    }

    @Test
    public void testBidReduction() {
        CPLEXUtils.SOLVER.initializeSolveParams();
        BundleBid bid1 = new BundleBid(BigDecimal.valueOf(6), Sets.newHashSet(A), "1");
        BundleBid bid2 = new BundleBid(BigDecimal.valueOf(20), Sets.newHashSet(B), "2");
        BundleBid bid3 = new BundleBid(BigDecimal.valueOf(20), Sets.newHashSet(C), "3");
        BundleBid bid4 = new BundleBid(BigDecimal.valueOf(10), Sets.newHashSet(B,C), "4");
        BundleBid bid6 = new BundleBid(BigDecimal.valueOf(22), Sets.newHashSet(A,B), "5");

        Bids bids = new Bids();
        Bidder bidder1 = new SimpleBidder("B" + 1);
        bids.setBid(bidder1, new Bid(Sets.newHashSet(bid1,bid4)));
        Bidder bidder2 = new SimpleBidder("B" + 2);
        bids.setBid(bidder2, new Bid(Sets.newHashSet(bid2,bid6)));
        Bidder bidder3 = new SimpleBidder("B" + 3);
        bids.setBid(bidder3, new Bid(Sets.newHashSet(bid3)));
        auctionInstance = new AuctionInstance(bids);
        VCGReferencePointFactory rpFacory = new VCGReferencePointFactory();
        CCGMechanismFactory quadratic = new VariableNormCCGFactory(rpFacory, Norm.MANHATTAN, Norm.EUCLIDEAN);

        CCGAuction ccgAuction = quadratic.getMechanism(auctionInstance);
        AuctionResult outcome = ccgAuction.getAuctionResult();
        assertThat(outcome.getAllocation().getTotalAllocationValue()).isEqualByComparingTo("46");
        assertThat(outcome.getPayment().getTotalPayments()).isEqualByComparingTo("6");

    }

    @Test
    public void testSimpleWinnerDetermination() {
        BundleBid bid1 = new BundleBid(BigDecimal.valueOf(2), Sets.newHashSet(A), "1");
        BundleBid bid2 = new BundleBid(BigDecimal.valueOf(3), Sets.newHashSet(A, B, D), "2");
        BundleBid bid3 = new BundleBid(BigDecimal.valueOf(2), Sets.newHashSet(B, C), "3");
        BundleBid bid4 = new BundleBid(BigDecimal.valueOf(1), Sets.newHashSet(C, D), "4");

        Bids bids = new Bids();
        bids.setBid(new SimpleBidder("B" + 1), new Bid(Sets.newHashSet(bid1)));
        bids.setBid(new SimpleBidder("B" + 2), new Bid(Sets.newHashSet(bid2)));
        bids.setBid(new SimpleBidder("B" + 3), new Bid(Sets.newHashSet(bid3)));
        bids.setBid(new SimpleBidder("B" + 4), new Bid(Sets.newHashSet(bid4)));
        auctionInstance = new AuctionInstance(bids);
        Allocator wd = new ORWinnerDetermination(auctionInstance);

        Allocation result = wd.getAllocation();
        assertThat(result.getTotalAllocationValue().doubleValue()).isEqualTo(4);
        assertThat(result.allocationOf(new SimpleBidder("B" + 1)).getValue().doubleValue()).isEqualTo(2);
        assertThat(result.allocationOf(new SimpleBidder("B" + 3)).getValue().doubleValue()).isEqualTo(2);
        assertThat(result.allocationOf(new SimpleBidder("B" + 2)).getValue()).isZero();
        assertThat(result.allocationOf(new SimpleBidder("B" + 4)).getValue()).isZero();
    }


    @Test
    public void testMediumWinnerDetermination() {
        BundleBid bid0 = new BundleBid(BigDecimal.valueOf(1795.51), Sets.newHashSet(C, D), "0");
        BundleBid bid1 = new BundleBid(BigDecimal.valueOf(894.644), Sets.newHashSet(D), "1");
        BundleBid bid2 = new BundleBid(BigDecimal.valueOf(1633.62), Sets.newHashSet(A, B), "2");
        BundleBid bid3 = new BundleBid(BigDecimal.valueOf(997.064), Sets.newHashSet(C), "3");
        BundleBid bid4 = new BundleBid(BigDecimal.valueOf(1751.26), Sets.newHashSet(B, C), "4");
        BundleBid bid5 = new BundleBid(BigDecimal.valueOf(1779.42), Sets.newHashSet(A, E), "5");
        BundleBid bid6 = new BundleBid(BigDecimal.valueOf(843.716), Sets.newHashSet(B), "6");
        BundleBid bid7 = new BundleBid(BigDecimal.valueOf(762.093), Sets.newHashSet(E), "7");
        BundleBid bid8 = new BundleBid(BigDecimal.valueOf(893.983), Sets.newHashSet(A), "8");
        BundleBid bid9 = new BundleBid(BigDecimal.valueOf(1816.69), Sets.newHashSet(A, C), "9");
        Bids bids = new Bids();
        bids.setBid(new SimpleBidder("B" + 0), new Bid(Sets.newHashSet(bid0)));
        bids.setBid(new SimpleBidder("B" + 1), new Bid(Sets.newHashSet(bid1)));
        bids.setBid(new SimpleBidder("B" + 2), new Bid(Sets.newHashSet(bid2)));
        bids.setBid(new SimpleBidder("B" + 3), new Bid(Sets.newHashSet(bid3)));
        bids.setBid(new SimpleBidder("B" + 4), new Bid(Sets.newHashSet(bid4)));
        bids.setBid(new SimpleBidder("B" + 5), new Bid(Sets.newHashSet(bid5)));
        bids.setBid(new SimpleBidder("B" + 6), new Bid(Sets.newHashSet(bid6)));
        bids.setBid(new SimpleBidder("B" + 7), new Bid(Sets.newHashSet(bid7)));
        bids.setBid(new SimpleBidder("B" + 8), new Bid(Sets.newHashSet(bid8)));
        bids.setBid(new SimpleBidder("B" + 9), new Bid(Sets.newHashSet(bid9)));
        auctionInstance = new AuctionInstance(bids);
        Allocator wd = new ORWinnerDetermination(auctionInstance);

        Allocation result = wd.getAllocation();
        assertThat(result.getTotalAllocationValue().doubleValue()).isEqualTo(4514.844);
        assertThat(result.allocationOf(new SimpleBidder("B" + 1)).getValue().doubleValue()).isEqualTo(894.644);
        assertThat(result.allocationOf(new SimpleBidder("B" + 2)).getValue()).isZero();
        assertThat(result.allocationOf(new SimpleBidder("B" + 4)).getValue()).isZero();
        assertThat(result.allocationOf(new SimpleBidder("B" + 0)).getValue()).isZero();

    }
}
