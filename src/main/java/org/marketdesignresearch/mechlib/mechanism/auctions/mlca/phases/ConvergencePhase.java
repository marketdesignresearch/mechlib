package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValuePair;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionPhase;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRound;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRoundBuilder;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.ConvergenceInteraction;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.impl.DefaultConvergenceInteraction;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.ElicitationEconomy;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.refinement.EfficiencyGuaranteeEfficiencyInfoCreator;
import org.marketdesignresearch.mechlib.winnerdetermination.XORWinnerDetermination;
import org.marketdesignresearch.mechlib.winnerdetermination.XORWinnerDeterminationWithExclucedBundles;

import com.google.common.collect.Sets;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConvergencePhase implements AuctionPhase<BundleBoundValueBids> {

	public static BigDecimal DEFAULT_FIRST_EPSILON = BigDecimal.valueOf(0.005);
	// add some slack due to inexact arithmetics
	public static BigDecimal DEFAULT_EFFICIENCY_TOLERANCE = BigDecimal.valueOf(0.5);
	
	public static int DEFAULT_NUMBER_OF_BUNDLES = 4;
	
	@Getter
	@Setter
	private BigDecimal firstEpsilon = DEFAULT_FIRST_EPSILON;
	@Getter
	@Setter
	private EfficiencyGuaranteeEfficiencyInfoCreator efficiencyInfoCreator = new EfficiencyGuaranteeEfficiencyInfoCreator(DEFAULT_EFFICIENCY_TOLERANCE);
	@Getter
	@Setter
	private int numberOfBundles = 4;
	
	@Override
	public AuctionRoundBuilder<BundleBoundValueBids> createNextRoundBuilder(Auction<BundleBoundValueBids> auction) {
		BigDecimal epsilon = firstEpsilon;
		
		// decrease epsilon to force convergence
		AuctionRound<BundleBoundValueBids> round = auction.getLastRound();
		if(round != null && round instanceof ConvergenceAuctionRound) {
			ConvergenceAuctionRound convergenceRound = (ConvergenceAuctionRound) round;
			epsilon = convergenceRound.getEpsilon().divide(BigDecimal.valueOf(2),RoundingMode.HALF_UP);
		}
		
		log.info("Epsilon: {}", epsilon);
		
		Map<UUID, ConvergenceInteraction> interactions = new LinkedHashMap<>();
		
		BundleBoundValueBids latestBids = auction.getLatestAggregatedBids();
		Allocation lowerBoundAllocation = new XORWinnerDetermination(latestBids).getAllocation();
		BundleExactValueBids perturbedBids = latestBids.getPerturbedBids(lowerBoundAllocation);
		
		for(Bidder bidder : auction.getDomain().getBidders()) {
			BundleBoundValueBid bid = latestBids.getBid(bidder);
			
			Set<Bundle> querySet = new LinkedHashSet<>();
			Set<Bundle> excludedSet = new LinkedHashSet<>();
			
			BundleBoundValuePair lowerBoundAllocatedValuePair = bid.getBidForBundle(lowerBoundAllocation.allocationOf(bidder).getBundle());
			if(lowerBoundAllocatedValuePair != null) {
				if(this.verifiyIntervalSize(lowerBoundAllocatedValuePair, epsilon)) {
					excludedSet.add(lowerBoundAllocatedValuePair.getBundle());
				} else {
					querySet.add(lowerBoundAllocatedValuePair.getBundle());
				}
			} else {
				excludedSet.add(lowerBoundAllocation.allocationOf(bidder).getBundle());
			}
			
			while(querySet.size() < this.numberOfBundles && excludedSet.size() + querySet.size() < bid.getBundleBids().size()) {
				Allocation perturbedAllocation = new XORWinnerDeterminationWithExclucedBundles(perturbedBids, Map.of(bidder, Sets.union(querySet, excludedSet))).getAllocation();
				BundleBoundValuePair perturbedPair = bid.getBidForBundle(perturbedAllocation.allocationOf(bidder).getBundle());
				if(perturbedPair != null) {
					if(this.verifiyIntervalSize(perturbedPair, epsilon)) {
						excludedSet.add(perturbedPair.getBundle());
					} else {
						querySet.add(perturbedPair.getBundle());
					}
				} else {
					excludedSet.add(perturbedAllocation.allocationOf(bidder).getBundle());
				}
			}
			
			System.out.println("Number of Bundles: "+querySet.size());
			System.out.println("Number of excluded Bundles: "+excludedSet.size());
			
			interactions.put(bidder.getId(), new DefaultConvergenceInteraction(bidder.getId(), auction, querySet, epsilon, bid));
		}
		
		return new ConvergenceAuctionRoundBuilder(auction, interactions);
	}

	@Override
	public boolean phaseFinished(Auction<BundleBoundValueBids> auction) {
		// Run at least one convergence round
		if(auction.getLastRound() == null || ! (auction.getLastRound() instanceof ConvergenceAuctionRound)) {
			return false;
		}
		
		return this.getEfficiencyInfoCreator().getEfficiencyInfo(auction.getLatestAggregatedBids(), List.of(new ElicitationEconomy(auction.getDomain()))).isConverged();
	}
	
	private boolean verifiyIntervalSize(BundleBoundValuePair pair, BigDecimal epsilon) {
		BigDecimal interval = pair.getUpperBound().subtract(pair.getLowerBound());
		// Do not divide by zero
		if(pair.getUpperBound().compareTo(BigDecimal.ZERO) == 0) {
			return true;
		}
		BigDecimal uncertainty = interval.divide(pair.getUpperBound(),RoundingMode.HALF_UP);
		return uncertainty.compareTo(epsilon) <= 0;
	}

	@Override
	public String getType() {
		return "Convergence Phase";
	}

}