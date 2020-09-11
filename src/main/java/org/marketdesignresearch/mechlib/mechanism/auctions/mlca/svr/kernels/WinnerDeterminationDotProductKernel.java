package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr.kernels;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.BundleEntry;
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
 * WDP based on SVR dot product kernel 
 * 
 * @author Gianluca Brero
 * @author Manuel Beyeler
 * @see KernelDotProductExponential
 * @see KernelDotProductPolynomial
 */
public class WinnerDeterminationDotProductKernel extends WinnerDeterminationWithExcludedBundles {

	private KernelDotProduct kernel;

	public WinnerDeterminationDotProductKernel(Domain domain, ElicitationEconomy economy,
			BundleExactValueBids supportVectors, Map<Bidder, Set<Bundle>> excludedBundles, KernelDotProduct kernel,
			double timelimit) {
		super(domain, economy, supportVectors, excludedBundles, timelimit);
		this.kernel = kernel;
	}

	@Override
	protected IMIP createKernelSpecificWinnerDeterminationProgram() {
		Map<UUID, Map<Bundle, Map<Integer, Variable>>> bidderSVSizeVariables = new LinkedHashMap<>();

		MIPWrapper mipWrapper = MIPWrapper.makeNewMaxMIP();
		int varNum = 0;
		for (UUID b : this.getEconomy().getBidders()) {
			bidderGoodVariables.put(b, new LinkedHashMap<Good, Variable>());
			bidderSVSizeVariables.put(b, new LinkedHashMap<Bundle, Map<Integer, Variable>>());

			// Insert variables, one per each good
			for (Good good : this.getDomain().getGoods()) {
				bidderGoodVariables.get(b).put(good, mipWrapper.makeNewBooleanVar("Bidder Good " + (++varNum)));
			}

			// Define objective
			for (BundleExactValuePair bv : this.getSupportVectors().getBid(this.getBidder(b)).getBundleBids()) {
				bidderSVSizeVariables.get(b).put(bv.getBundle(), new LinkedHashMap<Integer, Variable>());
				Constraint cSize = mipWrapper.beginNewEQConstraint(1);
				Constraint cSet = mipWrapper.beginNewEQConstraint(1);
				int svSize = bv.getBundle().getTotalAmount();
				for (int i = 0; i <= svSize; i++) {
					bidderSVSizeVariables.get(b).get(bv.getBundle()).put(i,
							mipWrapper.makeNewBooleanVar("SVSize " + (++varNum)));
					mipWrapper.addObjectiveTerm(bv.getAmount().doubleValue() * kernel.getValueGivenDotProduct(i),
							bidderSVSizeVariables.get(b).get(bv.getBundle()).get(i));
					cSize.addTerm(i + 1, bidderSVSizeVariables.get(b).get(bv.getBundle()).get(i));
					cSet.addTerm(1, bidderSVSizeVariables.get(b).get(bv.getBundle()).get(i));
				}
				// TODO: Implement multiple units!
				for (BundleEntry be : bv.getBundle().getBundleEntries()) {
					if (be.getAmount() == 1)
						cSize.addTerm(-1.0, bidderGoodVariables.get(b).get(be.getGood()));
					if (be.getAmount() > 1) {
						cSize.addTerm(-1.0, bidderGoodVariables.get(b).get(be.getGood()));
						throw new IllegalStateException("Generic Domains are not supported");
					}
				}
				mipWrapper.endConstraint(cSize);
				mipWrapper.endConstraint(cSet);
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