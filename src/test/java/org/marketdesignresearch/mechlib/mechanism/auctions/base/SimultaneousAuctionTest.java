package org.marketdesignresearch.mechlib.mechanism.auctions.base;

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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class SimultaneousAuctionTest {

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
                new BundleValue(BigDecimal.valueOf(15), Bundle.of(B)))));
        B2 = new XORBidder("B2", new XORValueFunction(Set.of(
                new BundleValue(BigDecimal.valueOf(12), Bundle.of(A)),
                new BundleValue(BigDecimal.valueOf(12), Bundle.of(B)))));
        domain = new SimpleXORDomain(List.of(B1, B2), List.of(A, B));
    }

    @Test
    public void testOutcome() {
        SimultaneousAuction auction = new SimultaneousAuction(domain, OutcomeRuleGenerator.VCG_XOR);
        log.info(auction.toString());
        auction.advanceRound();
        assertThat(auction.getOutcome().getAllocation().allocationOf(B1).getBundle()).isEqualTo(Bundle.of(B));
        assertThat(auction.getOutcome().getAllocation().allocationOf(B2).getBundle()).isEqualTo(Bundle.of(A));
        assertThat(auction.getRound(0).getBids().getBid(B1).getBundleBids()).hasSize(2);
        assertThat(auction.getRound(0).getBids().getBid(B1).getBidForBundle(Bundle.of(A)).getAmount()).isEqualByComparingTo("10");
        assertThat(auction.getRound(0).getBids().getBid(B1).getBidForBundle(Bundle.of(B)).getAmount()).isEqualByComparingTo("15");
        assertThat(auction.getRound(0).getBids().getBid(B2).getBundleBids()).hasSize(2);
        assertThat(auction.getRound(0).getBids().getBid(B2).getBidForBundle(Bundle.of(A)).getAmount()).isEqualByComparingTo("12");
        assertThat(auction.getRound(0).getBids().getBid(B2).getBidForBundle(Bundle.of(B)).getAmount()).isEqualByComparingTo("12");
        assertThatThrownBy(auction::advanceRound).isExactlyInstanceOf(IllegalStateException.class);
    }

}
