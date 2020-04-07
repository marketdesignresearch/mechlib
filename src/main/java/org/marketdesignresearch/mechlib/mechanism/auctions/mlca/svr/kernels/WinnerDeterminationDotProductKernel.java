package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr.kernels;

import java.util.HashMap;
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

public class WinnerDeterminationDotProductKernel extends WinnerDeterminationWithExcludedBundles {

	private KernelDotProduct kernel;

	public WinnerDeterminationDotProductKernel(Domain domain, ElicitationEconomy economy, BundleExactValueBids supportVectors,
			Map<Bidder, Set<Bundle>> excludedBundles, KernelDotProduct kernel) {
		super(domain, economy, supportVectors, excludedBundles);
		this.kernel = kernel;
	}

	protected IMIP getSpecificMIP() {
		Map<UUID, Map<Bundle, Map<Integer, Variable>>> bidderSVSizeVariables = new HashMap<>();

		MIPWrapper mipWrapper = MIPWrapper.makeNewMaxMIP();

		for (UUID b : this.getEconomy().getBidders()) {
			bidderGoodVariables.put(b, new HashMap<Good, Variable>());
			bidderSVSizeVariables.put(b, new HashMap<Bundle, Map<Integer, Variable>>());

			// Insert variables, one per each good
			for (Good good : this.getDomain().getGoods()) {
				bidderGoodVariables.get(b).put(good,
						mipWrapper.makeNewBooleanVar(b.toString() + " Good " + good.toString()));
			}

			// Define objective
			for (BundleExactValuePair bv : this.getSupportVectors().getBid(this.getBidder(b)).getBundleBids()) {
				bidderSVSizeVariables.get(b).put(bv.getBundle(), new HashMap<Integer, Variable>());
				Constraint cSize = mipWrapper.beginNewEQConstraint(1);
				Constraint cSet = mipWrapper.beginNewEQConstraint(1);
				int svSize = bv.getBundle().getTotalAmount();
				for (int i = 0; i <= svSize; i++) {
					bidderSVSizeVariables.get(b).get(bv.getBundle()).put(i,
							mipWrapper.makeNewBooleanVar(b.toString() + " " + bv.toString() + " Size " + i));
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
						System.out.println("I am ignoring multiple units!");
						cSize.addTerm(-1.0, bidderGoodVariables.get(b).get(be.getGood()));
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