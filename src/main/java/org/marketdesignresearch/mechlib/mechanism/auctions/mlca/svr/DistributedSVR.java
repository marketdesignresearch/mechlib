package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.MachineLearningComponent;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr.kernels.Kernel;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public abstract class DistributedSVR<T extends BundleValueBids<?>> implements MachineLearningComponent<T> {

	@Getter
	private final SupportVectorSetup setup;

	
	public DistributedSVR(Kernel kernel) {
		this(new SupportVectorSetup(kernel));
	}
	
	
	/*
	private void setLearningMeasures(AuctionSetting setting, ElicitationMetaInfo metainfo, Allocation inferredEffAllocation, SupportVectors svr) {
		double learningErrorEffAllocation = 0d;
		double learningErrorTempAllocation = 0d;
		
		for (Bidder bidder : setting.getBidders()) {
			Bundle bundle = SimMetainfo.efficientAllocation.allocationOf(bidder);
			learningErrorEffAllocation += Math.abs(bidder.getValueForBundle(bundle) - svr.svrs.get(bidder).getModelPredictedValueOf(bundle));
			
			bundle = inferredEffAllocation.allocationOf(bidder);
			learningErrorTempAllocation += Math.abs(svr.svrs.get(bidder).getModelPredictedValueOf(bundle) - bidder.getValueForBundle(bundle));			
		}	
		
		metainfo.addLearningErrorEffAllocation(learningErrorEffAllocation/SimMetainfo.efficientAllocation.getTotalAllocationTrueValue());
		metainfo.addLearningErrorTempAllocation(learningErrorTempAllocation/SimMetainfo.efficientAllocation.getTotalAllocationTrueValue());
		metainfo.addEfficiencyTempAllocation(inferredEffAllocation.getTotalAllocationTrueValue()/SimMetainfo.efficientAllocation.getTotalAllocationTrueValue());
		metainfo.addSolveTimeTempAllocation(inferredEffAllocation.getMetainfo().getSolveTime());
		metainfo.setHitTimeLimit(inferredEffAllocation.getMetainfo().getHitTimeLimit());
		
		if (SimMetainfo.numSamplesAverageError!=0) setAverageLearningError(setting,metainfo,svr);		
	}

	private void setAverageLearningError(AuctionSetting setting, ElicitationMetaInfo metainfo, SupportVectors svr) {
			double avLearningError = 0d;
			for (Bidder bidder : setting.getBidders()) {
				if (SimMetainfo.numSamplesAverageError==-1)
					for (int i=0; i<Math.pow(2, setting.getNumItems()); i++) {
						Bundle bundle = setting.getBundle(i);		
						avLearningError += Math.abs(svr.svrs.get(bidder).getModelPredictedValueOf(bundle) - bidder.getValueForBundle(bundle))/(Math.pow(2, setting.getNumItems())*setting.getBidders().size());						
					}
				else {
					for(long i=0; i<SimMetainfo.numSamplesAverageError; i++){
						Bundle bundle = setting.getGoods().getRandomBundleStandardSeed(i);	
						avLearningError += Math.abs(svr.svrs.get(bidder).getModelPredictedValueOf(bundle) - bidder.getValueForBundle(bundle))/(SimMetainfo.numSamplesAverageError*setting.getBidders().size());
					}
				}
			}
			metainfo.addAverageLearningError(avLearningError);		
	}
	*/
}
