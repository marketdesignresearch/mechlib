package org.marketdesignresearch.mechlib.mechanism.auctions.sequential;

import lombok.extern.slf4j.Slf4j;
import org.junit.BeforeClass;
import org.junit.Test;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.SimpleGood;
import org.marketdesignresearch.mechlib.core.SimpleXORDomain;
import org.marketdesignresearch.mechlib.core.bidder.XORBidder;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.BundleValue;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.XORValueFunction;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRuleGenerator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class SequentialAuctionTest {

    private static SimpleXORDomain domain;
    private static Good A;
    private static Good B;
    private static XORBidder B1;
    private static XORBidder B2;

    @BeforeClass
    public static void setUp() {
        A = new SimpleGood("A");
        B = new SimpleGood("B");
        B1 = new XORBidder("B1", new XORValueFunction(Set.of(
                new BundleValue(BigDecimal.valueOf(10), Bundle.of(A)),
                new BundleValue(BigDecimal.valueOf(10), Bundle.of(B)))));
        B2 = new XORBidder("B2", new XORValueFunction(Set.of(
                new BundleValue(BigDecimal.valueOf(12), Bundle.of(A)),
                new BundleValue(BigDecimal.valueOf(12), Bundle.of(B)))));
        domain = new SimpleXORDomain(List.of(B1, B2), List.of(A, B));
    }

    @Test
    public void testCCAWithCATSAuction() {
        SequentialAuction auction = new SequentialAuction(domain, OutcomeRuleGenerator.VCG_XOR);
        log.info(auction.toString());
        auction.advanceRound();
        assertThat(auction.getOutcome().getAllocation().allocationOf(B1).getBundle()).isEqualTo(Bundle.EMPTY);
        assertThat(auction.getOutcome().getAllocation().allocationOf(B2).getBundle()).isEqualTo(Bundle.of(A));
        auction.advanceRound();
        assertThat(auction.getOutcome().getAllocation().allocationOf(B1).getBundle()).isEqualTo(Bundle.of(B));
        assertThat(auction.getOutcome().getAllocation().allocationOf(B2).getBundle()).isEqualTo(Bundle.of(A));
    }

}
