package org.marketdesignresearch.mechlib.auction.pvm;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.marketdesignresearch.mechlib.domain.*;
import org.marketdesignresearch.mechlib.domain.bidder.ORBidder;
import org.marketdesignresearch.mechlib.domain.bidder.value.BundleValue;
import org.marketdesignresearch.mechlib.domain.bidder.value.ORValue;
import org.marketdesignresearch.mechlib.mechanisms.MechanismResult;
import org.marketdesignresearch.mechlib.mechanisms.MechanismType;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;


@Slf4j
public class PVMTest {

    @Test
    public void testPVMManually() {
        SimpleGood goodA = new SimpleGood("A");
        SimpleGood goodB = new SimpleGood("B");
        SimpleGood goodC = new SimpleGood("C");
        SimpleGood goodD = new SimpleGood("D");
        Bundle A = Bundle.of(Sets.newHashSet(goodA));
        Bundle B = Bundle.of(Sets.newHashSet(goodB));
        Bundle C = Bundle.of(Sets.newHashSet(goodC));
        Bundle D = Bundle.of(Sets.newHashSet(goodD));
        Bundle AB = Bundle.of(Sets.newHashSet(goodA, goodB));
        Bundle AC = Bundle.of(Sets.newHashSet(goodA, goodC));
        Bundle AD = Bundle.of(Sets.newHashSet(goodA, goodD));
        Bundle BC = Bundle.of(Sets.newHashSet(goodB, goodC));
        Bundle BD = Bundle.of(Sets.newHashSet(goodB, goodD));
        Bundle CD = Bundle.of(Sets.newHashSet(goodC, goodD));
        Bundle ABC = Bundle.of(Sets.newHashSet(goodA, goodB, goodC));
        Bundle ABD = Bundle.of(Sets.newHashSet(goodA, goodB, goodD));
        Bundle ACD = Bundle.of(Sets.newHashSet(goodA, goodC, goodD));
        Bundle BCD = Bundle.of(Sets.newHashSet(goodB, goodC, goodD));
        Bundle ABCD = Bundle.of(Sets.newHashSet(goodA, goodB, goodC, goodD));

        ORValue value1 = new ORValue();
        value1.addBundleValue(new BundleValue(BigDecimal.valueOf(1), A));
        value1.addBundleValue(new BundleValue(BigDecimal.valueOf(3), B));
        value1.addBundleValue(new BundleValue(BigDecimal.valueOf(2), C));
        value1.addBundleValue(new BundleValue(BigDecimal.valueOf(10), D));
        ORValue value2 = new ORValue();
        value2.addBundleValue(new BundleValue(BigDecimal.valueOf(10), A));
        value2.addBundleValue(new BundleValue(BigDecimal.valueOf(2), B));
        value2.addBundleValue(new BundleValue(BigDecimal.valueOf(3), C));
        value2.addBundleValue(new BundleValue(BigDecimal.valueOf(6), D));
        ORValue value3 = new ORValue();
        value3.addBundleValue(new BundleValue(BigDecimal.valueOf(3), A));
        value3.addBundleValue(new BundleValue(BigDecimal.valueOf(1), B));
        value3.addBundleValue(new BundleValue(BigDecimal.valueOf(10), C));
        value3.addBundleValue(new BundleValue(BigDecimal.valueOf(1), D));
        ORValue value4 = new ORValue();
        value4.addBundleValue(new BundleValue(BigDecimal.valueOf(2), A));
        value4.addBundleValue(new BundleValue(BigDecimal.valueOf(10), B));
        value4.addBundleValue(new BundleValue(BigDecimal.valueOf(2), C));
        value4.addBundleValue(new BundleValue(BigDecimal.valueOf(3), D));
        ORBidder bidder1 = new ORBidder("1", value1);
        ORBidder bidder2 = new ORBidder("2", value2);
        ORBidder bidder3 = new ORBidder("3", value3);
        ORBidder bidder4 = new ORBidder("4", value4);
        Domain domain = new SimpleORDomain(
                Lists.newArrayList(bidder1, bidder2, bidder3, bidder4),
                Lists.newArrayList(goodA, goodB, goodC, goodD));
        PVMAuction auction = new PVMAuction(domain, MechanismType.VCG_XOR);
        assertThat(auction.restrictedBids()).isEmpty();
        auction.nextRound();
        while(!auction.finished()) {
            assertThat(auction.getDomain().getBidders().size()).isEqualTo(auction.restrictedBids().keySet().size());
            auction.nextRound();
        }
        log.info("PVM terminated.");
        MechanismResult mechanismResult = auction.getMechanismResult();
        Allocation pvmAllocation = mechanismResult.getAllocation();
        Allocation efficientAllocation = domain.getEfficientAllocation();
        assertThat(pvmAllocation).isEqualTo(efficientAllocation);
    }

}
