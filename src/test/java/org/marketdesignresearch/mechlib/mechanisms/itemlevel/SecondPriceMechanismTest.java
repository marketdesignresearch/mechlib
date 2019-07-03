package org.marketdesignresearch.mechlib.mechanisms.itemlevel;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.marketdesignresearch.mechlib.auction.Auction;
import org.marketdesignresearch.mechlib.domain.*;
import org.marketdesignresearch.mechlib.domain.bid.Bid;
import org.marketdesignresearch.mechlib.domain.bid.Bids;
import org.marketdesignresearch.mechlib.domain.bid.SingleItemBids;
import org.marketdesignresearch.mechlib.domain.bidder.Bidder;
import org.marketdesignresearch.mechlib.domain.bidder.XORBidder;
import org.marketdesignresearch.mechlib.mechanisms.MechanismResult;
import org.marketdesignresearch.mechlib.mechanisms.MechanismType;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class SecondPriceMechanismTest {

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
        BundleBid bid1A = new BundleBid(BigDecimal.valueOf(2), Sets.newHashSet(item), "1A");
        BundleBid bid1B = new BundleBid(BigDecimal.valueOf(7), Sets.newHashSet(item), "1B");
        BundleBid bid2A = new BundleBid(BigDecimal.valueOf(1), Sets.newHashSet(item), "2A");
        BundleBid bid2B = new BundleBid(BigDecimal.valueOf(10), Sets.newHashSet(item), "2B");
        BundleBid bid3A = new BundleBid(BigDecimal.valueOf(3), Sets.newHashSet(item), "3A");
        BundleBid bid3B = new BundleBid(BigDecimal.valueOf(1), Sets.newHashSet(item), "3B");
        BundleBid bid3C = new BundleBid(BigDecimal.valueOf(8), Sets.newHashSet(item), "3C");
        Bids bids = new Bids();
        bids.setBid(bidder1, new Bid(Sets.newHashSet(bid1A, bid1B)));
        bids.setBid(bidder2, new Bid(Sets.newHashSet(bid2A, bid2B)));
        bids.setBid(bidder3, new Bid(Sets.newHashSet(bid3A, bid3B, bid3C)));
        SingleItemBids singleItemBids = new SingleItemBids(bids);
        MechanismResult mechanismResult = new SecondPriceMechanism(singleItemBids).getMechanismResult();
        checkResult(mechanismResult, bidder2, bid2B, BigDecimal.valueOf(8));
    }

    @Test
    public void testSimpleSecondPriceAuctionWithWrapper() {
        SimpleXORDomain domain = new SimpleXORDomain(Lists.newArrayList(bidder1, bidder2, bidder3), Lists.newArrayList(item));
        Auction auction = new Auction(domain, MechanismType.SECOND_PRICE);

        BundleBid bid1A = new BundleBid(BigDecimal.valueOf(2), Sets.newHashSet(item), "1A");
        BundleBid bid1B = new BundleBid(BigDecimal.valueOf(7), Sets.newHashSet(item), "1B");
        BundleBid bid2A = new BundleBid(BigDecimal.valueOf(1), Sets.newHashSet(item), "2A");
        BundleBid bid2B = new BundleBid(BigDecimal.valueOf(10), Sets.newHashSet(item), "2B");
        BundleBid bid3A = new BundleBid(BigDecimal.valueOf(3), Sets.newHashSet(item), "3A");
        BundleBid bid3B = new BundleBid(BigDecimal.valueOf(1), Sets.newHashSet(item), "3B");
        BundleBid bid3C = new BundleBid(BigDecimal.valueOf(8), Sets.newHashSet(item), "3C");
        Bids bids = new Bids();
        bids.setBid(bidder1, new Bid(Sets.newHashSet(bid1A, bid1B)));
        bids.setBid(bidder2, new Bid(Sets.newHashSet(bid2A, bid2B)));
        bids.setBid(bidder3, new Bid(Sets.newHashSet(bid3A, bid3B, bid3C)));
        SingleItemBids singleItemBids = new SingleItemBids(bids);

        auction.addRound(singleItemBids);
        MechanismResult mechanismResult = auction.getMechanismResult();
        checkResult(mechanismResult, bidder2, bid2B, BigDecimal.valueOf(8));
    }

    @Test
    public void testSecondPriceAuctionTwoWinningBids() {
        BundleBid bid1A = new BundleBid(BigDecimal.valueOf(2), Sets.newHashSet(item), "1A");
        BundleBid bid1B = new BundleBid(BigDecimal.valueOf(10), Sets.newHashSet(item), "1B");
        BundleBid bid2A = new BundleBid(BigDecimal.valueOf(1), Sets.newHashSet(item), "2A");
        BundleBid bid2B = new BundleBid(BigDecimal.valueOf(10), Sets.newHashSet(item), "2B");
        BundleBid bid3A = new BundleBid(BigDecimal.valueOf(3), Sets.newHashSet(item), "3A");
        BundleBid bid3B = new BundleBid(BigDecimal.valueOf(1), Sets.newHashSet(item), "3B");
        BundleBid bid3C = new BundleBid(BigDecimal.valueOf(8), Sets.newHashSet(item), "3C");
        Bids bids = new Bids();
        bids.setBid(bidder1, new Bid(Sets.newHashSet(bid1A, bid1B)));
        bids.setBid(bidder2, new Bid(Sets.newHashSet(bid2A, bid2B)));
        bids.setBid(bidder3, new Bid(Sets.newHashSet(bid3A, bid3B, bid3C)));
        SingleItemBids singleItemBids = new SingleItemBids(bids);
        MechanismResult mechanismResult = new SecondPriceMechanism(singleItemBids).getMechanismResult();
        checkResult(mechanismResult, bidder1, bid1B, BigDecimal.TEN);
    }

    @Test
    public void testSecondPriceAuctionNoBidder() {
        SingleItemBids bids = new SingleItemBids(new Bids());
        MechanismResult mechanismResult = new SecondPriceMechanism(bids).getMechanismResult();
        Allocation allocation = mechanismResult.getAllocation();
        assertThat(allocation.getTotalAllocationValue()).isZero();
        Payment payment = mechanismResult.getPayment();
        assertThat(payment.getTotalPayments()).isZero();
        assertThat(payment.getPaymentMap()).isEmpty();
    }

    @Test
    public void testSecondPriceAuctionSingleBidder() {
        BundleBid bid1A = new BundleBid(BigDecimal.valueOf(2), Sets.newHashSet(item), "1A");
        BundleBid bid1B = new BundleBid(BigDecimal.valueOf(10), Sets.newHashSet(item), "1B");
        BundleBid bid2A = new BundleBid(BigDecimal.valueOf(1), Sets.newHashSet(item), "2A");
        BundleBid bid2B = new BundleBid(BigDecimal.valueOf(10), Sets.newHashSet(item), "2B");
        BundleBid bid3A = new BundleBid(BigDecimal.valueOf(3), Sets.newHashSet(item), "3A");
        BundleBid bid3B = new BundleBid(BigDecimal.valueOf(1), Sets.newHashSet(item), "3B");
        BundleBid bid3C = new BundleBid(BigDecimal.valueOf(8), Sets.newHashSet(item), "3C");
        Bids bids = new Bids();
        bids.setBid(bidder1, new Bid(Sets.newHashSet(bid1A, bid1B, bid2A, bid2B, bid3A, bid3B, bid3C)));
        SingleItemBids singleItemBids = new SingleItemBids(bids);
        MechanismResult mechanismResult = new SecondPriceMechanism(singleItemBids).getMechanismResult();
        checkResult(mechanismResult, bidder1, bid2B, BigDecimal.ZERO); // FIXME: Had accepted bid
    }

    @Test
    public void testInvalidSingleGoodAuction() {
        BundleBid bid1 = new BundleBid(BigDecimal.valueOf(2), Sets.newHashSet(item), "1");
        BundleBid bid2 = new BundleBid(BigDecimal.valueOf(10), Sets.newHashSet(new SimpleGood("item2")), "2");
        BundleBid bid3 = new BundleBid(BigDecimal.valueOf(15), Sets.newHashSet(item, new SimpleGood("item3", 2, false)), "3");

        // FIXME
        //assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> bid3.getBundle().getSingleGood());
        //assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> new Bundle(ImmutableMap.of(item, 3)).getSingleGood());

        Bids bidsOnTwoDifferentGoods = new Bids();
        bidsOnTwoDifferentGoods.setBid(bidder1, new Bid(Sets.newHashSet(bid1)));
        bidsOnTwoDifferentGoods.setBid(bidder2, new Bid(Sets.newHashSet(bid2)));
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> new SingleItemBids(bidsOnTwoDifferentGoods));

        Bids bidsOnComplexGood = new Bids();
        bidsOnComplexGood.setBid(bidder1, new Bid(Sets.newHashSet(bid1)));
        bidsOnComplexGood.setBid(bidder3, new Bid(Sets.newHashSet(bid3)));
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> new SingleItemBids(bidsOnComplexGood));
    }

    private void checkResult(MechanismResult mechanismResult, Bidder expectedWinner, BundleBid expectedWinningBid, BigDecimal expectedPayment) {
        Set<Bidder> losers = Sets.newHashSet(bidder1, bidder2, bidder3);
        losers.remove(expectedWinner);
        Allocation allocation = mechanismResult.getAllocation();
        Payment payment = mechanismResult.getPayment();
        assertThat(allocation.getTotalAllocationValue()).isEqualTo(expectedWinningBid.getAmount());
        for (Bidder bidder : losers) {
            assertThat(allocation.allocationOf(bidder).getAcceptedBids()).isEmpty();
            assertThat(allocation.allocationOf(bidder).getValue()).isZero();
            assertThat(allocation.allocationOf(bidder).getBundle().getBundleEntries()).isEmpty();
            assertThat(payment.paymentOf(bidder).getAmount()).isZero();
        }

        assertThat(allocation.allocationOf(expectedWinner).getAcceptedBids()).hasSize(1);
        BundleBid winningBid = allocation.allocationOf(expectedWinner).getAcceptedBids().iterator().next();
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
