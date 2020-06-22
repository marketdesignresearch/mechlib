package org.marketdesignresearch.mechlib.mechanism.auctions.pvm;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.Test;
import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.SimpleGood;
import org.marketdesignresearch.mechlib.core.SimpleORDomain;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValuePair;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.bidder.ORBidder;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.BundleValue;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.ORValueFunction;
import org.marketdesignresearch.mechlib.mechanism.auctions.pvm.ml.MLAlgorithm;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRuleGenerator;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;


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
        List<Bundle> bundles = Lists.newArrayList(A, B, C, D, AB, AC, AD, BC, BD, CD, ABC, ABD, ACD, BCD, ABCD);

        Set<BundleValue> value1 = new HashSet<>();
        value1.add(new BundleValue(BigDecimal.valueOf(1), A));
        value1.add(new BundleValue(BigDecimal.valueOf(3), B));
        value1.add(new BundleValue(BigDecimal.valueOf(2), C));
        value1.add(new BundleValue(BigDecimal.valueOf(10), D));
        Set<BundleValue> value2 = new HashSet<>();
        value2.add(new BundleValue(BigDecimal.valueOf(10), A));
        value2.add(new BundleValue(BigDecimal.valueOf(2), B));
        value2.add(new BundleValue(BigDecimal.valueOf(3), C));
        value2.add(new BundleValue(BigDecimal.valueOf(6), D));
        Set<BundleValue> value3 = new HashSet<>();
        value3.add(new BundleValue(BigDecimal.valueOf(3), A));
        value3.add(new BundleValue(BigDecimal.valueOf(1), B));
        value3.add(new BundleValue(BigDecimal.valueOf(10), C));
        value3.add(new BundleValue(BigDecimal.valueOf(1), D));
        Set<BundleValue> value4 = new HashSet<>();
        value4.add(new BundleValue(BigDecimal.valueOf(2), A));
        value4.add(new BundleValue(BigDecimal.valueOf(10), B));
        value4.add(new BundleValue(BigDecimal.valueOf(2), C));
        value4.add(new BundleValue(BigDecimal.valueOf(3), D));
        ORBidder bidder1 = new ORBidder("1", new ORValueFunction(value1));
        ORBidder bidder2 = new ORBidder("2", new ORValueFunction(value2));
        ORBidder bidder3 = new ORBidder("3", new ORValueFunction(value3));
        ORBidder bidder4 = new ORBidder("4", new ORValueFunction(value4));
        Domain domain = new SimpleORDomain(
                Lists.newArrayList(bidder1, bidder2, bidder3, bidder4),
                Lists.newArrayList(goodA, goodB, goodC, goodD));
        PVMAuction auction = new PVMAuction(domain, MLAlgorithm.Type.LINEAR_REGRESSION, OutcomeRuleGenerator.VCG_XOR, 6);
        auction.advanceRound();
        while(!auction.finished()) {
            assertThat(auction.getDomain().getBidders().size()).isEqualTo(auction.restrictedBids().keySet().size());
            for (Bidder bidder : domain.getBidders()) {
                log.info("----- Elicitation in round {} for bidder {}", auction.getNumberOfRounds(), bidder.getName());
                for (Bundle bundle : bundles) {
                    Optional<BundleExactValuePair> reported = auction.getLatestAggregatedBids(bidder).getBundleBids().stream().filter(bbid -> bbid.getBundle().equals(bundle)).findAny();
                    log.info("- Bundle {}", bundle);
                    log.info("\t*\tTrue Value: {}", bidder.getValue(bundle).setScale(2, RoundingMode.HALF_UP));
                    log.info("\t*\tReported Value: {}", reported.isPresent() ? reported.get().getAmount().setScale(2, RoundingMode.HALF_UP) : "-");
                    log.info("\t*\tInferred Value: {}", auction.getInferredValue(bidder, bundle).setScale(2, RoundingMode.HALF_UP));
                }
            }
            auction.advanceRound();
        }
        log.info("PVM terminated.");
        Outcome outcome = auction.getOutcome();
        Allocation pvmAllocation = outcome.getAllocation();
        Allocation efficientAllocation = domain.getEfficientAllocation();
        assertThat(pvmAllocation).isEqualTo(efficientAllocation);
    }

}
