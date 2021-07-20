package org.marketdesignresearch.mechlib.core.bidder.strategy.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValuePair;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.ValueFunction;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.DIARRefinement;

public class DIARRefiner extends AutomatedRefiner<DIARRefinement> {

	/**
	 * Even though working with BigDecimal arithmetics some slack is needed to avoid
	 * validation errors later
	 */
	public static BigDecimal roundingDelta = BigDecimal.valueOf(1E-8d);

	public static double overallRefinement = 0;

	// Standard Deviation for overestimating epsilon
	public static BigDecimal gamma = BigDecimal.valueOf(0.01);

	/**
	 * Sums all refinement performed by any DIARRefiner during this program
	 * execution
	 */
	public static synchronized void addAmount(BigDecimal bigDecimal) {
		overallRefinement += bigDecimal.doubleValue();
	}

	@Override
	// see thesis for details
	public BundleBoundValueBid refineBids(DIARRefinement type, ValueFunction b, BundleBoundValueBid activeBids,
			BundleBoundValueBid refinedBids, Prices prices, Bundle provisionalTrade, Random random) {

		BundleBoundValueBid returnBid = refinedBids.copy();

		BigDecimal epsilon = type.getEpsilon();

		List<ImmutablePair<Bundle, BigDecimal>> diarErrors = this.getDIARErrorOrder(activeBids, prices,
				provisionalTrade);

		BigDecimal trueValueProvisional = b.getValue(provisionalTrade);

		BigDecimal lambda = epsilon.multiply(BigDecimal.valueOf(this.getNextGuassianLikeDouble(random)));

		for (ImmutablePair<Bundle, BigDecimal> diarError : diarErrors) {

			// check if already reduced by other refinements
			BigDecimal previousReduction = BigDecimal.ZERO;
			if (!(provisionalTrade.getTotalAmount() == 0)) {
				previousReduction = returnBid.getBidForBundle(provisionalTrade).getLowerBound()
						.subtract(activeBids.getBidForBundle(provisionalTrade).getLowerBound());
			}
			if (diarError.left != null) {
				previousReduction = previousReduction.add(activeBids.getBidForBundle(diarError.left).getUpperBound()
						.subtract(returnBid.getBidForBundle(diarError.left).getUpperBound()));

			}
			if (previousReduction.compareTo(epsilon.add(roundingDelta)) >= 0)
				break;

			BigDecimal trueValue = BigDecimal.ZERO;
			if (diarError.left != null)
				trueValue = b.getValue(diarError.left);
			BigDecimal provisonalOffset = BigDecimal.ZERO;
			// check if provisioinalTrade is empty
			if (!(provisionalTrade.getTotalAmount() == 0)) {
				provisonalOffset = trueValueProvisional
						.subtract(activeBids.getBidForBundle(provisionalTrade).getLowerBound());
			}

			BigDecimal diarBundleOffset = BigDecimal.ZERO;

			BigDecimal beta = BigDecimal.valueOf(random.nextGaussian()).multiply(gamma).multiply(epsilon).abs();

			if (diarError.left != null)
				diarBundleOffset = activeBids.getBidForBundle(diarError.left).getUpperBound().subtract(trueValue);
			if (diarBundleOffset.add(provisonalOffset).compareTo(epsilon.add(beta)) > 0
					&& (diarError.left != null || !(provisionalTrade.getTotalAmount() == 0))) {
				// reduce error if possible
				BigDecimal neededReduction = epsilon.add(roundingDelta);
				if (diarError.left != null)
					neededReduction = neededReduction.subtract((activeBids.getBidForBundle(diarError.left)
							.getUpperBound().subtract(returnBid.getBidForBundle(diarError.left).getUpperBound())));
				if (!(provisionalTrade.getTotalAmount() == 0)) {
					neededReduction = neededReduction.subtract(returnBid.getBidForBundle(provisionalTrade)
							.getLowerBound().subtract(activeBids.getBidForBundle(provisionalTrade).getLowerBound()));
				}

				BigDecimal possibleReduction = BigDecimal.ZERO;
				if (diarError.left != null)
					possibleReduction = returnBid.getBidForBundle(diarError.left).getUpperBound().subtract(trueValue);
				if (!(provisionalTrade.getTotalAmount() == 0)) {
					possibleReduction = possibleReduction.add(
							trueValueProvisional.subtract(returnBid.getBidForBundle(provisionalTrade).getLowerBound()));
				}

				BigDecimal offset = possibleReduction.subtract(neededReduction)
						.multiply(BigDecimal.valueOf(this.getNextGuassianLikeDouble(random)));

				BigDecimal reduction = BigDecimal.ZERO;
				if (!(provisionalTrade.getTotalAmount() == 0)) {
					reduction = trueValueProvisional
							.subtract(returnBid.getBidForBundle(provisionalTrade).getLowerBound()).subtract(offset)
							.max(BigDecimal.ZERO);
					reduction = reduction.min(neededReduction);
					BigDecimal newLower = returnBid.getBidForBundle(provisionalTrade).getLowerBound().add(reduction);
					newLower = newLower.max(returnBid.getBidForBundle(provisionalTrade).getLowerBound());
					newLower = newLower.min(b.getValue(provisionalTrade));
					returnBid.addBundleBid(new BundleBoundValuePair(newLower,
							returnBid.getBidForBundle(provisionalTrade).getUpperBound(), provisionalTrade,
							UUID.randomUUID().toString()));
				}
				if (diarError.left != null) {
					BigDecimal newUpper = returnBid.getBidForBundle(diarError.left).getUpperBound()
							.subtract(neededReduction).add(reduction);
					newUpper = newUpper.min(returnBid.getBidForBundle(diarError.left).getUpperBound());
					newUpper = newUpper.max(b.getValue(diarError.left));
					returnBid.addBundleBid(
							new BundleBoundValuePair(returnBid.getBidForBundle(diarError.left).getLowerBound(),
									newUpper, diarError.left, UUID.randomUUID().toString()));
				}

				DIARRefiner.addAmount(neededReduction);
				break;
			} else {
				// show that reduction is not possible
				BigDecimal rangeA = BigDecimal.ZERO;
				if (!(provisionalTrade.getTotalAmount() == 0)) {
					rangeA = returnBid.getBidForBundle(provisionalTrade).getUpperBound()
							.subtract(returnBid.getBidForBundle(provisionalTrade).getLowerBound());
					rangeA = BigDecimal.ZERO.max(rangeA);
				}
				BigDecimal rangeX = BigDecimal.ZERO;
				if (diarError.left != null) {
					rangeX = returnBid.getBidForBundle(diarError.left).getUpperBound()
							.subtract(returnBid.getBidForBundle(diarError.left).getLowerBound());
				}

				if (rangeA.add(rangeX).add(roundingDelta).compareTo(epsilon) > 0) {

					BigDecimal newRangeX = lambda.max(epsilon.subtract(rangeA));
					if (diarError.left == null) {
						newRangeX = BigDecimal.ZERO;
					} else {
						newRangeX = newRangeX.min(returnBid.getBidForBundle(diarError.left).getUpperBound()
								.subtract(returnBid.getBidForBundle(diarError.left).getLowerBound()));
					}
					BigDecimal newRangeA = epsilon.subtract(newRangeX);

					BigDecimal kapa = BigDecimal.valueOf(this.getNextGuassianLikeDouble(random));
					if (!(provisionalTrade.getTotalAmount() == 0)) {
						BigDecimal newUpper = b.getValue(provisionalTrade).add(kapa.multiply(newRangeA))
								.subtract(roundingDelta).max(returnBid.getBidForBundle(provisionalTrade).getLowerBound()
										.add(newRangeA).subtract(roundingDelta));
						newUpper = newUpper.min(returnBid.getBidForBundle(provisionalTrade).getUpperBound());
						newUpper = newUpper.max(b.getValue(provisionalTrade));

						BigDecimal newLower = b.getValue(provisionalTrade)
								.subtract(BigDecimal.ONE.subtract(kapa).multiply(newRangeA)).add(roundingDelta)
								.min(returnBid.getBidForBundle(provisionalTrade).getUpperBound().subtract(newRangeA)
										.add(roundingDelta));
						newLower = newLower.max(returnBid.getBidForBundle(provisionalTrade).getLowerBound());
						newLower = newLower.min(b.getValue(provisionalTrade));

						DIARRefiner.addAmount(
								returnBid.getBidForBundle(provisionalTrade).getUpperBound().subtract(newUpper));
						DIARRefiner.addAmount(
								newLower.subtract(returnBid.getBidForBundle(provisionalTrade).getLowerBound()));
						returnBid.addBundleBid(new BundleBoundValuePair(newLower, newUpper, provisionalTrade,
								UUID.randomUUID().toString()));
					}

					kapa = BigDecimal.valueOf(this.getNextGuassianLikeDouble(random));
					if (diarError.left != null) {
						BigDecimal newUpper = b.getValue(diarError.left).add(kapa.multiply(newRangeX))
								.subtract(roundingDelta).max(returnBid.getBidForBundle(diarError.left).getLowerBound()
										.add(newRangeX).subtract(roundingDelta));
						newUpper = newUpper.min(returnBid.getBidForBundle(diarError.left).getUpperBound());
						newUpper = newUpper.max(b.getValue(diarError.left));

						BigDecimal newLower = b.getValue(diarError.left)
								.subtract(BigDecimal.ONE.subtract(kapa).multiply(newRangeX)).add(roundingDelta)
								.min(returnBid.getBidForBundle(diarError.left).getUpperBound().subtract(newRangeX)
										.add(roundingDelta));
						newLower = newLower.max(returnBid.getBidForBundle(diarError.left).getLowerBound());
						newLower = newLower.min(b.getValue(diarError.left));

						DIARRefiner.addAmount(
								returnBid.getBidForBundle(diarError.left).getUpperBound().subtract(newUpper));
						DIARRefiner.addAmount(
								newLower.subtract(returnBid.getBidForBundle(diarError.left).getLowerBound()));
						returnBid.addBundleBid(new BundleBoundValuePair(newLower, newUpper, diarError.left,
								UUID.randomUUID().toString()));
					}
				}
			}
		}

		return returnBid;
	}

	private List<ImmutablePair<Bundle, BigDecimal>> getDIARErrorOrder(BundleValueBid<BundleBoundValuePair> activeBids,
			Prices prices, Bundle provisionalTrade) {

		BigDecimal worstCaseProvisionalTrade = getWorstCaseProvisionalProfit(activeBids, prices, provisionalTrade);

		List<ImmutablePair<Bundle, BigDecimal>> diarError = new ArrayList<>();

		for (BundleBoundValuePair bid : activeBids.getBundleBids()) {
			// Error for provisional Trade can be ignored as it can not be improved by
			// definition
			if (!bid.getBundle().equals(provisionalTrade)) {
				diarError.add(new ImmutablePair<>(bid.getBundle(), bid.getUpperBound()
						.subtract(prices.getPrice(bid.getBundle()).getAmount()).subtract(worstCaseProvisionalTrade)));
			}
		}

		// empty bundle
		diarError.add(new ImmutablePair<>(null, BigDecimal.ZERO.subtract(worstCaseProvisionalTrade)));

		diarError.sort((e1, e2) -> -e1.getRight().compareTo(e2.getRight()));

		return diarError;
	}

	private BigDecimal getWorstCaseProvisionalProfit(BundleValueBid<BundleBoundValuePair> activeBids, Prices prices,
			Bundle provisionalTrade) {
		BigDecimal worstCaseProvisionalTrade = BigDecimal.ZERO;
		if (provisionalTrade != null && provisionalTrade.getTotalAmount() > 0) {
			worstCaseProvisionalTrade = activeBids.getBidForBundle(provisionalTrade).getLowerBound()
					.subtract(prices.getPrice(provisionalTrade).getAmount());
		}
		return worstCaseProvisionalTrade;
	}

}
