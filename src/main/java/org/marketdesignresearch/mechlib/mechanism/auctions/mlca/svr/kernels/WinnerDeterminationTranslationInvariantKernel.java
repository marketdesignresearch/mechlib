package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr.kernels;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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

import edu.harvard.econcs.jopt.solver.IMIP;
import edu.harvard.econcs.jopt.solver.mip.Constraint;
import edu.harvard.econcs.jopt.solver.mip.MIPWrapper;
import edu.harvard.econcs.jopt.solver.mip.Variable;

/**
 * WDP based on SVR gaussian kernel
 * 
 * @author Gianluca Brero
 * @author Manuel Beyeler
 * @see KernelGaussian
 */
public class WinnerDeterminationTranslationInvariantKernel extends WinnerDeterminationWithExcludedBundles {
	/*
	 * Doesn't work with multiple units!
	 */

	private KernelGaussian kernel;

	public WinnerDeterminationTranslationInvariantKernel(Domain domain, ElicitationEconomy economy,
			BundleExactValueBids supportVectorsPerBidder, Map<Bidder, Set<Bundle>> excludedBids,
			KernelGaussian kernelGaussian, double timelimit) {
		super(domain, economy, supportVectorsPerBidder, excludedBids, timelimit);
		this.kernel = kernelGaussian;
	}

	@Override
	protected IMIP createKernelSpecificWinnerDeterminationProgram() {

		Map<UUID, Map<Bundle, Map<Good, Variable>>> bidderSVDiffVariables = new LinkedHashMap<>();

		MIPWrapper mipWrapper = MIPWrapper.makeNewMaxMIP();

		int varNum = 0;
		for (UUID b : this.getEconomy().getBidders()) {
			bidderGoodVariables.put(b, new LinkedHashMap<>());
			bidderSVDiffVariables.put(b, new LinkedHashMap<>());

			// Insert variables, one per each good
			for (Good good : this.getDomain().getGoods()) {
				bidderGoodVariables.get(b).put(good, mipWrapper.makeNewBooleanVar("Bidder Good " + (++varNum)));
			}

			for (BundleExactValuePair bv : this.getSupportVectors().getBid(this.getBidder(b)).getBundleBids()) {
				bidderSVDiffVariables.get(b).put(bv.getBundle(), new LinkedHashMap<>());
				Constraint cSet = mipWrapper.beginNewEQConstraint(1 + bv.getBundle().getTotalAmount());
				Constraint cSize = mipWrapper.beginNewEQConstraint(1);

				int goodIdx = 0;
				for (Good good : this.getDomain().getGoods()) {
					bidderSVDiffVariables.get(b).get(bv.getBundle()).put(good,
							mipWrapper.makeNewBooleanVar("SV Diff " + (++varNum)));
					mipWrapper.addObjectiveTerm(bv.getAmount().doubleValue() * kernel.getValueGivenDifference(goodIdx),
							bidderSVDiffVariables.get(b).get(bv.getBundle()).get(good));
					cSet.addTerm(goodIdx + 1, bidderSVDiffVariables.get(b).get(bv.getBundle()).get(good));
					cSize.addTerm(1, bidderSVDiffVariables.get(b).get(bv.getBundle()).get(good));
					goodIdx++;
				}

				Set<Good> complementSet = new LinkedHashSet<>();
				// TODO: Implement multiple units!
				for (Good good : this.getDomain().getGoods()) {
					if (bv.getBundle().countGood(good) == 0)
						complementSet.add(good);
					if (bv.getBundle().countGood(good) > 1)
						System.out.println("I am ignoring multiple units!");
				}
				for (Good good : this.getDomain().getGoods()) {
					if (bv.getBundle().countGood(good) == 1)
						cSet.addTerm(+1.0, bidderGoodVariables.get(b).get(good));
					if (bv.getBundle().countGood(good) > 1) {
						cSet.addTerm(+1.0, bidderGoodVariables.get(b).get(good));
						throw new IllegalStateException("Generic domains are not supported");
					}
				}
				for (Good good : complementSet)
					cSet.addTerm(-1.0, bidderGoodVariables.get(b).get(good));

				mipWrapper.endConstraint(cSet);
				mipWrapper.endConstraint(cSize);
			}
		}

		for (Good good : this.getDomain().getGoods()) {
			Constraint c = mipWrapper.beginNewLEQConstraint(1);
			for (UUID b : this.getEconomy().getBidders()) {
				c.addTerm(1, bidderGoodVariables.get(b).get(good));
			}
			mipWrapper.endConstraint(c);
		}

		return mipWrapper;
	}
}
