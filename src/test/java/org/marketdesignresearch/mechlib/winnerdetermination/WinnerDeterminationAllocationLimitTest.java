package org.marketdesignresearch.mechlib.winnerdetermination;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.SimpleGood;
import org.marketdesignresearch.mechlib.core.allocationlimits.AllocationLimit;
import org.marketdesignresearch.mechlib.core.allocationlimits.BundleSizeAllocationLimit;
import org.marketdesignresearch.mechlib.core.allocationlimits.BundleSizeAndGoodAllocationLimit;
import org.marketdesignresearch.mechlib.core.allocationlimits.GoodAllocationLimit;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValuePair;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.bidder.XORBidder;
import org.marketdesignresearch.mechlib.outcomerules.AllocationRule;

import com.google.common.collect.Sets;

public class WinnerDeterminationAllocationLimitTest {
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
	public void testSimpleORWinnerDeterminationGoodAllocationLimit() {
		BundleExactValuePair bid11 = new BundleExactValuePair(BigDecimal.valueOf(1), Sets.newHashSet(A), "1");
		BundleExactValuePair bid12 = new BundleExactValuePair(BigDecimal.valueOf(20), Sets.newHashSet(B, C), "2");
		BundleExactValuePair bid13 = new BundleExactValuePair(BigDecimal.valueOf(2), Sets.newHashSet(C), "3");
		BundleExactValuePair bid21 = new BundleExactValuePair(BigDecimal.valueOf(2), Sets.newHashSet(B, C), "4");
		BundleExactValuePair bid22 = new BundleExactValuePair(BigDecimal.valueOf(1), Sets.newHashSet(B), "5");
		AllocationLimit limit1 = new GoodAllocationLimit(List.of(A, B, C), List.of(A, C));
		Bidder bidder1 = new XORBidder("B" + 1, limit1);
		Bidder bidder2 = new XORBidder("B" + 2);

		BundleExactValueBids bids = new BundleExactValueBids();
		bids.setBid(bidder1, new BundleExactValueBid(Sets.newHashSet(bid11, bid12, bid13)));
		bids.setBid(bidder2, new BundleExactValueBid(Sets.newHashSet(bid21, bid22)));
		AllocationRule wd = new ORWinnerDetermination(bids);

		Allocation result = wd.getAllocation();
		assertThat(result.getTotalAllocationValue().doubleValue()).isEqualTo(4);
		assertThat(result.allocationOf(bidder1).getValue().doubleValue()).isEqualTo(3);
		assertThat(result.allocationOf(bidder2).getValue().doubleValue()).isEqualTo(1);
	}

	@Test
	public void testSimpleORWinnerDeterminationSizeAllocationLimit() {
		BundleExactValuePair bid11 = new BundleExactValuePair(BigDecimal.valueOf(1), Sets.newHashSet(A), "1");
		BundleExactValuePair bid12 = new BundleExactValuePair(BigDecimal.valueOf(20), Sets.newHashSet(B, C), "2");
		BundleExactValuePair bid13 = new BundleExactValuePair(BigDecimal.valueOf(2), Sets.newHashSet(C), "3");
		BundleExactValuePair bid21 = new BundleExactValuePair(BigDecimal.valueOf(2), Sets.newHashSet(B, C), "4");
		BundleExactValuePair bid22 = new BundleExactValuePair(BigDecimal.valueOf(1), Sets.newHashSet(B), "5");
		AllocationLimit limit1 = new BundleSizeAllocationLimit(2, List.of(A, B, C));
		Bidder bidder1 = new XORBidder("B" + 1, limit1);
		Bidder bidder2 = new XORBidder("B" + 2);

		BundleExactValueBids bids = new BundleExactValueBids();
		bids.setBid(bidder1, new BundleExactValueBid(Sets.newHashSet(bid11, bid12, bid13)));
		bids.setBid(bidder2, new BundleExactValueBid(Sets.newHashSet(bid21, bid22)));
		AllocationRule wd = new ORWinnerDetermination(bids);

		Allocation result = wd.getAllocation();
		assertThat(result.getTotalAllocationValue().doubleValue()).isEqualTo(20);
		assertThat(result.allocationOf(bidder1).getValue().doubleValue()).isEqualTo(20);
		assertThat(result.allocationOf(bidder2).getValue()).isZero();
	}

	@Test
	public void testSimpleORWinnerDeterminationSizeAndGoodAllocationLimit() {
		BundleExactValuePair bid11 = new BundleExactValuePair(BigDecimal.valueOf(1), Sets.newHashSet(A), "1");
		BundleExactValuePair bid12 = new BundleExactValuePair(BigDecimal.valueOf(20), Sets.newHashSet(B, C), "2");
		BundleExactValuePair bid13 = new BundleExactValuePair(BigDecimal.valueOf(3), Sets.newHashSet(C), "3");
		BundleExactValuePair bid14 = new BundleExactValuePair(BigDecimal.valueOf(3), Sets.newHashSet(D), "4");
		BundleExactValuePair bid21 = new BundleExactValuePair(BigDecimal.valueOf(2), Sets.newHashSet(B, C), "5");
		BundleExactValuePair bid22 = new BundleExactValuePair(BigDecimal.valueOf(1), Sets.newHashSet(B), "6");
		AllocationLimit limit1 = new BundleSizeAndGoodAllocationLimit(2, List.of(A, B, C, D), List.of(A, C, D));
		Bidder bidder1 = new XORBidder("B" + 1, limit1);
		Bidder bidder2 = new XORBidder("B" + 2);

		BundleExactValueBids bids = new BundleExactValueBids();
		bids.setBid(bidder1, new BundleExactValueBid(Sets.newHashSet(bid11, bid12, bid13, bid14)));
		bids.setBid(bidder2, new BundleExactValueBid(Sets.newHashSet(bid21, bid22)));
		AllocationRule wd = new ORWinnerDetermination(bids);

		Allocation result = wd.getAllocation();
		assertThat(result.getTotalAllocationValue().doubleValue()).isEqualTo(7);
		assertThat(result.allocationOf(bidder1).getValue().doubleValue()).isEqualTo(6);
		assertThat(result.allocationOf(bidder2).getValue().doubleValue()).isEqualTo(1);
	}

	@Test
	public void testSimpleXORWinnerDeterminationGoodAllocationLimit() {
		BundleExactValuePair bid11 = new BundleExactValuePair(BigDecimal.valueOf(1), Sets.newHashSet(A), "1");
		BundleExactValuePair bid12 = new BundleExactValuePair(BigDecimal.valueOf(20), Sets.newHashSet(B, C), "2");
		BundleExactValuePair bid13 = new BundleExactValuePair(BigDecimal.valueOf(3), Sets.newHashSet(C), "3");
		BundleExactValuePair bid21 = new BundleExactValuePair(BigDecimal.valueOf(2), Sets.newHashSet(B, C), "4");
		BundleExactValuePair bid22 = new BundleExactValuePair(BigDecimal.valueOf(1), Sets.newHashSet(B), "5");
		AllocationLimit limit1 = new GoodAllocationLimit(List.of(A, B, C), List.of(A, C));
		Bidder bidder1 = new XORBidder("B" + 1, limit1);
		Bidder bidder2 = new XORBidder("B" + 2);

		BundleExactValueBids bids = new BundleExactValueBids();
		bids.setBid(bidder1, new BundleExactValueBid(Sets.newHashSet(bid11, bid12, bid13)));
		bids.setBid(bidder2, new BundleExactValueBid(Sets.newHashSet(bid21, bid22)));
		AllocationRule wd = new XORWinnerDetermination(bids);

		Allocation result = wd.getAllocation();
		assertThat(result.getTotalAllocationValue().doubleValue()).isEqualTo(4);
		assertThat(result.allocationOf(bidder1).getValue().doubleValue()).isEqualTo(3);
		assertThat(result.allocationOf(bidder2).getValue().doubleValue()).isEqualTo(1);
	}

	@Test
	public void testSimpleXORWinnerDeterminationSizeAllocationLimit() {
		BundleExactValuePair bid11 = new BundleExactValuePair(BigDecimal.valueOf(30), Sets.newHashSet(A, B, C), "1");
		BundleExactValuePair bid12 = new BundleExactValuePair(BigDecimal.valueOf(20), Sets.newHashSet(B, C), "2");
		BundleExactValuePair bid13 = new BundleExactValuePair(BigDecimal.valueOf(2), Sets.newHashSet(C), "3");
		BundleExactValuePair bid21 = new BundleExactValuePair(BigDecimal.valueOf(2), Sets.newHashSet(B, C), "4");
		BundleExactValuePair bid22 = new BundleExactValuePair(BigDecimal.valueOf(1), Sets.newHashSet(B), "5");
		AllocationLimit limit1 = new BundleSizeAllocationLimit(2, List.of(A, B, C));
		Bidder bidder1 = new XORBidder("B" + 1, limit1);
		Bidder bidder2 = new XORBidder("B" + 2);

		BundleExactValueBids bids = new BundleExactValueBids();
		bids.setBid(bidder1, new BundleExactValueBid(Sets.newHashSet(bid11, bid12, bid13)));
		bids.setBid(bidder2, new BundleExactValueBid(Sets.newHashSet(bid21, bid22)));
		AllocationRule wd = new XORWinnerDetermination(bids);

		Allocation result = wd.getAllocation();
		assertThat(result.getTotalAllocationValue().doubleValue()).isEqualTo(20);
		assertThat(result.allocationOf(bidder1).getValue().doubleValue()).isEqualTo(20);
		assertThat(result.allocationOf(bidder2).getValue()).isZero();
	}

	@Test
	public void testSimpleXORWinnerDeterminationSizeAndGoodAllocationLimit() {
		BundleExactValuePair bid11 = new BundleExactValuePair(BigDecimal.valueOf(1), Sets.newHashSet(A), "1");
		BundleExactValuePair bid12 = new BundleExactValuePair(BigDecimal.valueOf(20), Sets.newHashSet(B, C), "2");
		BundleExactValuePair bid13 = new BundleExactValuePair(BigDecimal.valueOf(3), Sets.newHashSet(C), "3");
		BundleExactValuePair bid14 = new BundleExactValuePair(BigDecimal.valueOf(3), Sets.newHashSet(D), "4");
		BundleExactValuePair bid21 = new BundleExactValuePair(BigDecimal.valueOf(2), Sets.newHashSet(B, C), "5");
		BundleExactValuePair bid22 = new BundleExactValuePair(BigDecimal.valueOf(1), Sets.newHashSet(B), "6");
		AllocationLimit limit1 = new BundleSizeAndGoodAllocationLimit(2, List.of(A, B, C, D), List.of(A, C));
		Bidder bidder1 = new XORBidder("B" + 1, limit1);
		Bidder bidder2 = new XORBidder("B" + 2);

		BundleExactValueBids bids = new BundleExactValueBids();
		bids.setBid(bidder1, new BundleExactValueBid(Sets.newHashSet(bid11, bid12, bid13, bid14)));
		bids.setBid(bidder2, new BundleExactValueBid(Sets.newHashSet(bid21, bid22)));
		AllocationRule wd = new XORWinnerDetermination(bids);

		Allocation result = wd.getAllocation();
		assertThat(result.getTotalAllocationValue().doubleValue()).isEqualTo(4);
		assertThat(result.allocationOf(bidder1).getValue().doubleValue()).isEqualTo(3);
		assertThat(result.allocationOf(bidder2).getValue().doubleValue()).isEqualTo(1);
	}
}
