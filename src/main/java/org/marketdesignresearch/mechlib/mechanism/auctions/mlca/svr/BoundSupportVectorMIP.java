package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValuePair;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentation;

import edu.harvard.econcs.jopt.solver.IMIP;
import edu.harvard.econcs.jopt.solver.mip.Constraint;
import edu.harvard.econcs.jopt.solver.mip.MIPWrapper;
import edu.harvard.econcs.jopt.solver.mip.Variable;

/**
 * Trains support vectors based on given bids and kernel.
 * Formulation by Smola and Schoelkopf 2003
 * 
 * @author Gianluca Brero
 */
public class BoundSupportVectorMIP extends SupportVectorMIP<BundleBoundValueBid> {

	public BoundSupportVectorMIP(SupportVectorSetup setup, BundleBoundValueBid bid,
			MipInstrumentation instrumentation) {
		super(setup, bid, instrumentation);
	}

	@Override
	protected IMIP createMip() {
		MIPWrapper mipWrapper = MIPWrapper.makeNewMaxMIP();
		Constraint constraint = mipWrapper.beginNewEQConstraint(0.0);
		int indexQuery = 0;
		for (BundleBoundValuePair bid : this.getBid().getBundleBids()) {

			indexQuery++;
			Variable v1 = mipWrapper.makeNewDoubleVar("" + indexQuery);
			Variable v2 = mipWrapper.makeNewDoubleVar("" + -indexQuery);

			v1.setLowerBound(0);
			v1.setUpperBound(this.getInterpolationWeight());
			v2.setLowerBound(0);
			v2.setUpperBound(this.getInterpolationWeight());

			labeledDataVariables.put(indexQuery, v1);
			labeledDataVariables.put(-indexQuery, v2);

			double spread = bid.getUpperBound().subtract(bid.getLowerBound())
					.divide(BigDecimal.valueOf(2)).doubleValue();
			double mean = bid.getUpperBound().add(bid.getLowerBound())
					.divide(BigDecimal.valueOf(2)).doubleValue();
			mipWrapper.addObjectiveTerm(-spread - this.getInsensitivityThreshold(),
					labeledDataVariables.get(indexQuery));
			mipWrapper.addObjectiveTerm(-spread - this.getInsensitivityThreshold(),
					labeledDataVariables.get(-indexQuery));
			mipWrapper.addObjectiveTerm(mean, labeledDataVariables.get(indexQuery));
			mipWrapper.addObjectiveTerm(-mean, labeledDataVariables.get(-indexQuery));
		}
		mipWrapper.endConstraint(constraint);
		indexQuery = 0;

		for (BundleBoundValuePair bv1 : this.getBid().getBundleBids()) {
			indexQuery++;
			int indexQuery2 = 0;

			for (BundleBoundValuePair bv2 : this.getBid().getBundleBids()) {
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
