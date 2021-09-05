package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValuePair;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentation;

import edu.harvard.econcs.jopt.solver.IMIP;
import edu.harvard.econcs.jopt.solver.mip.MIPWrapper;
import edu.harvard.econcs.jopt.solver.mip.Variable;

/**
 * Trains support vectors based on given bids and kernel. Formulation by Smola
 * and Schoelkopf 2003
 * 
 * @author Gianluca Brero
 */
public class ExactSupportVectorMIP extends SupportVectorMIP<BundleExactValueBid> {

	public ExactSupportVectorMIP(SupportVectorSetup setup, BundleExactValueBid bid,
			MipInstrumentation mipInstrumentation) {
		super(setup, bid, mipInstrumentation);
	}

	@Override
	protected IMIP createMip() {
		MIPWrapper mipWrapper = MIPWrapper.makeNewMaxMIP();
		int indexQuery = 0;
		for (BundleExactValuePair bv : this.getBid().getBundleBids()) {

			indexQuery++;
			Variable v1 = mipWrapper.makeNewDoubleVar("" + indexQuery);
			Variable v2 = mipWrapper.makeNewDoubleVar("" + -indexQuery);
			double weight = this.getInterpolationWeight();

			v1.setLowerBound(0);
			v1.setUpperBound(weight);
			v2.setLowerBound(0);
			v2.setUpperBound(weight);

			labeledDataVariables.put(indexQuery, v1);
			labeledDataVariables.put(-indexQuery, v2);
			mipWrapper.addObjectiveTerm(-this.getInsensitivityThreshold(), labeledDataVariables.get(indexQuery));
			mipWrapper.addObjectiveTerm(-this.getInsensitivityThreshold(), labeledDataVariables.get(-indexQuery));
			mipWrapper.addObjectiveTerm(bv.getAmount().doubleValue(), labeledDataVariables.get(indexQuery));
			mipWrapper.addObjectiveTerm(-bv.getAmount().doubleValue(), labeledDataVariables.get(-indexQuery));
		}
		indexQuery = 0;
		for (BundleExactValuePair bv1 : this.getBid().getBundleBids()) {
			indexQuery++;
			int indexQuery2 = 0;
			for (BundleExactValuePair bv2 : this.getBid().getBundleBids()) {
				indexQuery2++;
				double kernelVal = this.getKernel().getValue(bv1.getBundle(), bv2.getBundle());
				mipWrapper.addObjectiveTerm(-0.5 * kernelVal, labeledDataVariables.get(indexQuery),
						labeledDataVariables.get(indexQuery2));
				mipWrapper.addObjectiveTerm(-0.5 * kernelVal, labeledDataVariables.get(-indexQuery),
						labeledDataVariables.get(-indexQuery2));
				mipWrapper.addObjectiveTerm(+0.5 * kernelVal, labeledDataVariables.get(indexQuery),
						labeledDataVariables.get(-indexQuery2));
				mipWrapper.addObjectiveTerm(+0.5 * kernelVal, labeledDataVariables.get(-indexQuery),
						labeledDataVariables.get(indexQuery2));
			}
		}
		return mipWrapper;
	}
}
