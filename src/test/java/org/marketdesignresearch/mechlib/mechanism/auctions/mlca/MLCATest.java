package org.marketdesignresearch.mechlib.mechanism.auctions.mlca;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.SimpleGood;
import org.marketdesignresearch.mechlib.core.SimpleXORDomain;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBid;
import org.marketdesignresearch.mechlib.core.bidder.XORBidder;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.BundleValue;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.XORValueFunction;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases.ExactMLQueryPhase;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases.ExactRandomQueryPhase;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr.ExactDistributedSVR;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr.SupportVectorSetup;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr.kernels.KernelDotProductExponential;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr.kernels.KernelDotProductPolynomial;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr.kernels.KernelGaussian;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr.kernels.KernelLinear;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr.kernels.KernelQuadratic;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRuleGenerator;

import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MLCATest {

	private static SimpleXORDomain domain;

	@BeforeClass
	public static void setUp() throws IOException {

		Random random = new Random(1);
		Set<SimpleGood> goods = new LinkedHashSet<>();

		for (int i = 0; i < 8; i++) {
			goods.add(new SimpleGood("" + i));
		}

		Set<Bundle> allBundles = Sets.powerSet(goods).stream().map(s -> Bundle.of(s)).collect(Collectors.toSet());
		List<XORBidder> bidders = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			// draw base values for each bidder
			Map<SimpleGood, Double> baseValue = new HashMap<SimpleGood, Double>();
			for (SimpleGood g : goods)
				baseValue.put(g, random.nextDouble() * 100);

			Set<BundleValue> values = new LinkedHashSet<>();
			for (Bundle bundle : allBundles)
				values.add(new BundleValue(BigDecimal.valueOf(
						// Sum Base Values
						bundle.getBundleEntries().stream().map(b -> baseValue.get(b.getGood()) * b.getAmount())
								.reduce(Double::sum).orElse(0d)
								// add some random synergies (also negative synergies are possible)
								* (0.9 + random.nextDouble() * bundle.getTotalAmount())),
						bundle));
			bidders.add(new XORBidder("Bidder " + i, new XORValueFunction(values)));
		}

		domain = new SimpleXORDomain(bidders, new ArrayList<SimpleGood>(goods));
	}

	@Test
	public void testMLCAWithLinearKernel() {
		ExactRandomQueryPhase initialPhase = new ExactRandomQueryPhase(30);
		ExactDistributedSVR svr = new ExactDistributedSVR(new SupportVectorSetup(100, 0.0001, new KernelLinear(0, 1)));
		ExactMLQueryPhase mlPhase = new ExactMLQueryPhase(svr, 50, 2);
		MLCAuction auction = new MLCAuction(domain, OutcomeRuleGenerator.VCG_XOR, initialPhase, mlPhase, 1l);
		Outcome outcome = auction.getOutcome();

		assertEquals(50, auction.getLatestAggregatedBids().getBids().stream().map(BundleExactValueBid::getBundleBids)
				.map(Set::size).reduce(Integer::max).orElse(0).intValue());
		assertEquals(50, auction.getLatestAggregatedBids().getBids().stream().map(BundleExactValueBid::getBundleBids)
				.map(Set::size).reduce(Integer::min).orElse(0).intValue());
		assertEquals(8, auction.getNumberOfRounds());
		assertTrue(domain.getEfficientAllocation().getTotalAllocationValue().doubleValue() > outcome.getAllocation()
				.getTotalAllocationValue().doubleValue());
	}

	@Test
	public void testMLCAWithQuadraticKernel() {
		ExactRandomQueryPhase initialPhase = new ExactRandomQueryPhase(30);
		ExactDistributedSVR svr = new ExactDistributedSVR(
				new SupportVectorSetup(100, 0.0001, new KernelQuadratic(0, 1, 0.01)));
		ExactMLQueryPhase mlPhase = new ExactMLQueryPhase(svr, 50, 2);
		MLCAuction auction = new MLCAuction(domain, OutcomeRuleGenerator.VCG_XOR, initialPhase, mlPhase, 1l);
		Outcome outcome = auction.getOutcome();

		assertEquals(8, auction.getNumberOfRounds());
		assertEquals(50, auction.getLatestAggregatedBids().getBids().stream().map(BundleExactValueBid::getBundleBids)
				.map(Set::size).reduce(Integer::max).orElse(0).intValue());
		assertEquals(50, auction.getLatestAggregatedBids().getBids().stream().map(BundleExactValueBid::getBundleBids)
				.map(Set::size).reduce(Integer::min).orElse(0).intValue());
		assertTrue(domain.getEfficientAllocation().getTotalAllocationValue().doubleValue() > outcome.getAllocation()
				.getTotalAllocationValue().doubleValue());
		log.info(outcome.toString());
	}

	@Test
	@Ignore
	public void testMLCAWithGaussianKernel() {
		// reference runtime approx 6 minute
		ExactRandomQueryPhase initialPhase = new ExactRandomQueryPhase(30);
		ExactDistributedSVR svr = new ExactDistributedSVR(
				new SupportVectorSetup(100, 0.0001, new KernelGaussian(1, 10)));
		ExactMLQueryPhase mlPhase = new ExactMLQueryPhase(svr, 32, 2);
		MLCAuction auction = new MLCAuction(domain, OutcomeRuleGenerator.VCG_XOR, initialPhase, mlPhase, 1l);
		Outcome outcome = auction.getOutcome();

		assertEquals(2, auction.getNumberOfRounds());
		assertEquals(32, auction.getLatestAggregatedBids().getBids().stream().map(BundleExactValueBid::getBundleBids)
				.map(Set::size).reduce(Integer::max).orElse(0).intValue());
		assertEquals(32, auction.getLatestAggregatedBids().getBids().stream().map(BundleExactValueBid::getBundleBids)
				.map(Set::size).reduce(Integer::min).orElse(0).intValue());
		assertTrue(domain.getEfficientAllocation().getTotalAllocationValue().doubleValue() > outcome.getAllocation()
				.getTotalAllocationValue().doubleValue());
		log.info(outcome.toString());
	}

	@Test
	public void testMLCAWithExponentialKernel() {
		ExactRandomQueryPhase initialPhase = new ExactRandomQueryPhase(30);
		ExactDistributedSVR svr = new ExactDistributedSVR(new KernelDotProductExponential(1, 10));
		ExactMLQueryPhase mlPhase = new ExactMLQueryPhase(svr, 32, 2);
		MLCAuction auction = new MLCAuction(domain, OutcomeRuleGenerator.VCG_XOR, initialPhase, mlPhase, 1l);
		Outcome outcome = auction.getOutcome();

		assertEquals(2, auction.getNumberOfRounds());
		assertEquals(32, auction.getLatestAggregatedBids().getBids().stream().map(BundleExactValueBid::getBundleBids)
				.map(Set::size).reduce(Integer::max).orElse(0).intValue());
		assertEquals(32, auction.getLatestAggregatedBids().getBids().stream().map(BundleExactValueBid::getBundleBids)
				.map(Set::size).reduce(Integer::min).orElse(0).intValue());
		assertTrue(domain.getEfficientAllocation().getTotalAllocationValue().doubleValue() > outcome.getAllocation()
				.getTotalAllocationValue().doubleValue());
		log.info(outcome.toString());
	}

	@Test
	public void testMLCAWithPolynomialKernel() {
		ExactRandomQueryPhase initialPhase = new ExactRandomQueryPhase(30);
		ExactDistributedSVR svr = new ExactDistributedSVR(
				new KernelDotProductPolynomial(new double[] { 0, 1, 0.1, 0.01, 0.001 }));
		ExactMLQueryPhase mlPhase = new ExactMLQueryPhase(svr, 32, 2);
		MLCAuction auction = new MLCAuction(domain, OutcomeRuleGenerator.VCG_XOR, initialPhase, mlPhase, 1l);
		Outcome outcome = auction.getOutcome();

		assertEquals(2, auction.getNumberOfRounds());
		assertEquals(32, auction.getLatestAggregatedBids().getBids().stream().map(BundleExactValueBid::getBundleBids)
				.map(Set::size).reduce(Integer::max).orElse(0).intValue());
		assertEquals(32, auction.getLatestAggregatedBids().getBids().stream().map(BundleExactValueBid::getBundleBids)
				.map(Set::size).reduce(Integer::min).orElse(0).intValue());
		assertTrue(domain.getEfficientAllocation().getTotalAllocationValue().doubleValue() > outcome.getAllocation()
				.getTotalAllocationValue().doubleValue());
		log.info(outcome.toString());
	}
}
