package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr.kernels;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValuePair;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.ElicitationEconomy;

import edu.harvard.econcs.jopt.solver.mip.Constraint;
import edu.harvard.econcs.jopt.solver.mip.MIPWrapper;
import edu.harvard.econcs.jopt.solver.mip.Variable;

/**
 * WDP based on SVR quadratic kernel
 * 
 * @author Gianluca Brero
 * @author Manuel Beyeler
 * @see KernelQuadratic
 */
public class WinnerDeterminationQuadraticKernel extends WinnerDeterminationWithExcludedBundles {

	private final KernelQuadratic kernel;

	public WinnerDeterminationQuadraticKernel(Domain domain, ElicitationEconomy economy,
			BundleExactValueBids supportVectors, Map<Bidder, Set<Bundle>> excludedBundles, KernelQuadratic kernel,
			double timelimit) {
		super(domain, economy, supportVectors, excludedBundles, timelimit);
		this.kernel = kernel;
	}

	@Override
	protected MIPWrapper createKernelSpecificWinnerDeterminationProgram() {
		MIPWrapper mipWrapper = MIPWrapper.makeNewMaxMIP();
		int varNum = 0;
		for (UUID b : this.getEconomy().getBidders()) {
			bidderGoodVariables.put(b, new LinkedHashMap<Good, Variable>());

			// Insert variables, one per each good
			for (Good good : this.getGoods()) {
				if (this.isGenericSetting()) {
					bidderGoodVariables.get(b).put(good, mipWrapper.makeNewIntegerVar("GoodVar " + (++varNum)));
					bidderGoodVariables.get(b).get(good).setLowerBound(0);
					bidderGoodVariables.get(b).get(good).setUpperBound(good.getQuantity());
				} else {
					bidderGoodVariables.get(b).put(good, mipWrapper.makeNewBooleanVar("GoodVar " + (++varNum)));
				}
			}

			// Define objective
			for (BundleExactValuePair bv : this.getSupportVectors().getBid(this.getBidder(b)).getBundleBids()) {
				for (int goodIdx = 0; goodIdx < this.getGoods().size(); goodIdx++) {
					Good good = this.getGoods().get(goodIdx);
					if (bv.getBundle().contains(good)) {
						mipWrapper.addObjectiveTerm(
								bv.getAmount().doubleValue() * kernel.getDeg1Coeff() * bv.getBundle().countGood(good),
								bidderGoodVariables.get(b).get(good));
						mipWrapper.addObjectiveTerm(
								bv.getAmount().doubleValue() * kernel.getDeg2Coeff()
										* Math.pow(bv.getBundle().countGood(good), 2),
								bidderGoodVariables.get(b).get(good), bidderGoodVariables.get(b).get(good));
						for (int goodIdx2 = goodIdx + 1; goodIdx2 < this.getGoods().size(); goodIdx2++) {
							Good good2 = this.getGoods().get(goodIdx2);
							if (bv.getBundle().contains(good2)) {
								mipWrapper.addObjectiveTerm(
										2 * bv.getAmount().doubleValue() * kernel.getDeg2Coeff()
												* bv.getBundle().countGood(good) * bv.getBundle().countGood(good2),
										bidderGoodVariables.get(b).get(good), bidderGoodVariables.get(b).get(good2));
							}
						}
					}
				}
			}
		}

		for (Good good : this.getGoods()) {
			Constraint c = mipWrapper.beginNewLEQConstraint(good.getQuantity());
			for (UUID b : this.getEconomy().getBidders()) {
				c.addTerm(1, bidderGoodVariables.get(b).get(good));
			}
			mipWrapper.endConstraint(c);
		}

		return mipWrapper;
	}
}
