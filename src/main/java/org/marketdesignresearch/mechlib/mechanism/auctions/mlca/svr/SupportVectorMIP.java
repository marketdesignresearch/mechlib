package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValuePair;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentation;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentationable;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentation.MipPurpose;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr.kernels.Kernel;
import org.marketdesignresearch.mechlib.utils.CPLEXUtils;

import com.google.common.collect.ImmutableMap;
import com.google.common.math.DoubleMath;

import edu.harvard.econcs.jopt.solver.IMIP;
import edu.harvard.econcs.jopt.solver.IMIPResult;
import edu.harvard.econcs.jopt.solver.SolveParam;
import edu.harvard.econcs.jopt.solver.client.SolverClient;
import edu.harvard.econcs.jopt.solver.mip.Variable;
import lombok.Getter;
import lombok.Setter;

public abstract class SupportVectorMIP<B extends BundleValueBid<?>> implements MipInstrumentationable{

	@Getter
	private final double interpolationWeight;
	@Getter
	private final double insensitivityThreshold;
	@Getter
	private final Kernel kernel;
	@Getter
	private final B bid;
	@Getter
	@Setter
	private MipInstrumentation mipInstrumentation;
	
	private IMIP mip;
	private BundleExactValueBid resultVectors;

	protected Map<Integer, Variable> labeledDataVariables = new HashMap<Integer, Variable>();

	public SupportVectorMIP(SupportVectorSetup setup, B bid, MipInstrumentation mipInstrumentation) {
		this.interpolationWeight = setup.getInterpolationWeight();
		this.insensitivityThreshold = setup.getInsensitivityThreshold();
		this.kernel = setup.getKernel();
		this.bid = bid;

		this.mip = this.createMip();
		this.mipInstrumentation = mipInstrumentation;
	}

	protected abstract IMIP createMip();
	
	private void solveMIP() {
		
		IMIPResult result;
		try {
			this.getMipInstrumentation().preMIP(MipPurpose.SUPPORT_VECTOR.name(), this.mip);
    		result = CPLEXUtils.SOLVER.solve(this.mip);
    		this.getMipInstrumentation().postMIP(MipPurpose.SUPPORT_VECTOR.name(), this.mip, result);
		} catch(RuntimeException e){
			// Try Barrier instead
			try {
			this.mip.setSolveParam(SolveParam.LP_OPTIMIZATION_ALG, 4);
			result = CPLEXUtils.SOLVER.solve(this.mip);
			} catch(RuntimeException e2) {
				// new CPlexMIPSolver().exportToDisk(mipWrapper, FileSystems.getDefault().getPath("mip", "train-"+System.currentTimeMillis()+".lp"));
				throw e2;
			}
		}
		this.adaptMIPResult(result);
	}
	
	private void adaptMIPResult(IMIPResult mipResult) {
		Map<Bundle,Double> fbMap = new HashMap<Bundle,Double>();
		int indexQuery = 0;
		for (BundleExactValuePair bv : this.bid.getBundleBids()){
			indexQuery ++;
			Bundle b = bv.getBundle();
			double value = mipResult.getValue(labeledDataVariables.get(indexQuery))-mipResult.getValue(labeledDataVariables.get(-indexQuery));
			if (fbMap.containsKey(b)) {double d = fbMap.get(b)+value; fbMap.put(b, d);}
			else fbMap.put(b, value);
		}
		ImmutableMap.Builder<Bundle,Double> fb = new ImmutableMap.Builder<>();
		for (Bundle b : fbMap.keySet()){
			if (!DoubleMath.fuzzyEquals(fbMap.get(b), 0.0, 1e-5)) fb.put(b, fbMap.get(b));
		}
		this.resultVectors = new BundleExactValueBid();
		fbMap.forEach((b,d)->this.resultVectors.addBundleBid(new BundleExactValuePair(BigDecimal.valueOf(d), b, b.toString())));
	}	

	public BundleExactValueBid getVectors() {
		if(this.resultVectors == null)
			this.solveMIP();
		return this.resultVectors;
	}
}
