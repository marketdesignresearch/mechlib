package org.marketdesignresearch.mechlib.winnerdetermination;

import java.util.Map;
import java.util.Set;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValuePair;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;

import edu.harvard.econcs.jopt.solver.mip.CompareType;
import edu.harvard.econcs.jopt.solver.mip.Constraint;

public class XORWinnerDeterminationWithExclucedBundles extends XORWinnerDetermination {

	public XORWinnerDeterminationWithExclucedBundles(BundleValueBids<?> bids,
			Map<Bidder, Set<Bundle>> excludedBundles) {
		super(bids);

		for (Map.Entry<Bidder, Set<Bundle>> e : excludedBundles.entrySet()) {
			for (Bundle b : e.getValue()) {
				if (b.equals(Bundle.EMPTY)) {
					Constraint c = new Constraint(CompareType.GEQ, 1);
					for (BundleExactValuePair allPair : this.getBids().getBid(e.getKey()).getBundleBids()) {
						if (!allPair.getBundle().equals(Bundle.EMPTY)) {
							c.addTerm(1, this.getBidVariable(allPair));
						}
					}

					this.winnerDeterminationProgram.add(c);
				} else {
					BundleExactValuePair pair = this.getBids().getBid(e.getKey()).getBidForBundle(b);
					this.getBidVariable(pair).setUpperBound(0);
				}
			}
		}
	}

}
