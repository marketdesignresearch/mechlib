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

import edu.harvard.econcs.jopt.solver.IMIP;
import edu.harvard.econcs.jopt.solver.mip.Constraint;
import edu.harvard.econcs.jopt.solver.mip.MIPWrapper;
import edu.harvard.econcs.jopt.solver.mip.Variable;

public class WinnerDeterminationLinearKernel extends WinnerDeterminationWithExcludedBundles {

	public WinnerDeterminationLinearKernel(Domain domain, ElicitationEconomy economy,
			BundleExactValueBids supportVectors, Map<Bidder, Set<Bundle>> excludedBundles, double timelimit) {
		super(domain, economy, supportVectors, excludedBundles, timelimit);
	}

	@Override
	protected IMIP createKernelSpecificWinnerDeterminationProgram() {
		MIPWrapper mipWrapper = MIPWrapper.makeNewMaxMIP();

		int varNum = 0;
		for (UUID b : this.getEconomy().getBidders()) {
			bidderGoodVariables.put(b, new LinkedHashMap<Good, Variable>());

			// Insert variables, one per each good
			for (Good good : this.getGoods()) {
				if (this.isGenericSetting()) {
					bidderGoodVariables.get(b).put(good, mipWrapper.makeNewIntegerVar("Bidder good " + (++varNum)));
					bidderGoodVariables.get(b).get(good).setLowerBound(0);
					bidderGoodVariables.get(b).get(good).setUpperBound(good.getQuantity());
				} else {
					// use boolean var for non generic for better performance
					bidderGoodVariables.get(b).put(good, mipWrapper.makeNewBooleanVar("Bidder good " + (++varNum)));
				}
			}

			// Define objective
			for (BundleExactValuePair bv : this.getSupportVectors().getBid(this.getBidder(b)).getBundleBids()) {
				for (Good good : this.getGoods())
					if (bv.getBundle().contains(good)) {
						mipWrapper.addObjectiveTerm(bv.getAmount().doubleValue() * bv.getBundle().countGood(good),
								bidderGoodVariables.get(b).get(good));
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