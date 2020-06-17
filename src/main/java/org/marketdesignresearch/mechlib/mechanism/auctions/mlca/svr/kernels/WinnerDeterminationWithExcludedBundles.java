package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr.kernels;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.BidderAllocation;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.BundleEntry;
import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentation.MipPurpose;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.ElicitationEconomy;
import org.marketdesignresearch.mechlib.metainfo.MetaInfo;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;

import com.google.common.collect.ImmutableMap;

import edu.harvard.econcs.jopt.solver.IMIP;
import edu.harvard.econcs.jopt.solver.IMIPResult;
import edu.harvard.econcs.jopt.solver.ISolution;
import edu.harvard.econcs.jopt.solver.mip.CompareType;
import edu.harvard.econcs.jopt.solver.mip.Constraint;
import edu.harvard.econcs.jopt.solver.mip.MIPWrapper;
import edu.harvard.econcs.jopt.solver.mip.Variable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class WinnerDeterminationWithExcludedBundles extends WinnerDetermination{

	// TODO ??
	public double relSolutionGap;
	
    protected Map<UUID,Map<Good, Variable>> bidderGoodVariables = new HashMap<>();
	
    @Getter
    private final Domain domain;
	@Getter
	private final ElicitationEconomy economy;
	@Getter
	private final BundleExactValueBids supportVectors;
	@Getter
	private final Map<Bidder,Set<Bundle>> excludedBundles;
	@Getter
	private final boolean genericSetting;
	
	private IMIP winnerDeterminationProgram = null;

	public WinnerDeterminationWithExcludedBundles(Domain domain, ElicitationEconomy economy, BundleExactValueBids supportVectors, Map<Bidder,Set<Bundle>> excludedBundles) {
		this.domain = domain;
		this.economy = economy;
		this.supportVectors = supportVectors;
		this.excludedBundles = excludedBundles;
		this.genericSetting = this.getDomain().getGoods().stream().map(g -> g.getQuantity() > 1).reduce(Boolean::logicalOr).get();
		this.setPurpose(MipPurpose.KERNEL_WINNERDETERMINATION.name());
	}
	
    /*
    protected Allocation solveWinnerDetermination(IMIP mipWrapper) {
    	mipWrapper.setSolveParam(SolveParam.TIME_LIMIT, cplexTimeLimit);	
    	if(mipGapTolerance>0.0) mipWrapper.setSolveParam(SolveParam.SOLUTION_POOL_MODE_4_RELATIVE_GAP_TOLERANCE, mipGapTolerance);
    	if(mipGapTolerance>0.0) mipWrapper.setSolveParam(SolveParam.RELATIVE_OBJ_GAP, mipGapTolerance);	
    	else mipWrapper.setSolveParam(SolveParam.RELATIVE_OBJ_GAP, 0.0);
 	
        IMIPResult mipResult = this.solve(mipWrapper);
        return adaptMIPResult(mipResult);
    }
    */
	
	protected Bidder getBidder(UUID id) {
		return this.domain.getBidders().stream().filter(b -> b.getId().equals(id)).findFirst().orElseThrow(NoSuchElementException::new);
	}
    
    protected Allocation adaptMIPResult(ISolution mipResult) {
    	
    	if (mipResult instanceof  IMIPResult)	relSolutionGap = ((IMIPResult) mipResult).getRelativeGap();
	    ImmutableMap.Builder<Bidder, BidderAllocation> trades = ImmutableMap.builder();
       
        for (UUID bidder : this.getEconomy().getBidders()) {
        	Set<BundleEntry> entries = new HashSet<>();
    		for (Good good : this.getGoods()) {
				double value = mipResult.getValue(bidderGoodVariables.get(bidder).get(good));
    			if (value >= 1 - 1e-3 && value <= 1 + 1e-3)
    				entries.add(new BundleEntry(good, (int) Math.floor(mipResult.getValue(bidderGoodVariables.get(bidder).get(good))+0.5)));
    		}
    		trades.put(this.getBidder(bidder), new BidderAllocation(BigDecimal.ZERO, new Bundle(entries), Collections.emptySet()));
    	}
      
        MetaInfo metaInfo = new MetaInfo();
        metaInfo.setNumberOfMIPs(1);
        metaInfo.setMipSolveTime(mipResult.getSolveTime());
        // if (TimeUnit.MILLISECONDS.toSeconds(mipResult.getSolveTime()) >= cplexTimeLimit)	metainfo.setHitTimeLimit(true);
        return new Allocation(BigDecimal.valueOf(mipResult.getObjectiveValue()),trades.build(),new BundleExactValueBids(),metaInfo);
    }  
    
    private IMIP createWinnerDeterminationProgram() {
    	IMIP mip = this.createKernelSpecificWinnerDeterminationProgram();
    	
    	for(Map.Entry<Bidder, Set<Bundle>> bidderEntry : this.excludedBundles.entrySet()) {
    		for (Bundle bundle : bidderEntry.getValue()) {
    			Constraint intCut = new Constraint(CompareType.LEQ, bundle.getTotalAmount() - 1 + 1e-8);
    			mip.add(intCut);
    			for (Good good : this.getGoods()) {
    				if (bundle.contains(good)) 
    					intCut.addTerm(1, this.bidderGoodVariables.get(bidderEntry.getKey().getId()).get(good));
    				else 
    					intCut.addTerm(-1, this.bidderGoodVariables.get(bidderEntry.getKey().getId()).get(good));
    			}
    		}
    	}
    	
    	return mip;
    }
    
    @Override
    protected IMIP getMIP() {
    	if(this.winnerDeterminationProgram == null)
    		this.winnerDeterminationProgram = this.createWinnerDeterminationProgram();
    	return this.winnerDeterminationProgram;
    }
    
    protected abstract IMIP createKernelSpecificWinnerDeterminationProgram();
    
    @Override
	public WinnerDetermination join(WinnerDetermination other) {
		throw new UnsupportedOperationException();
	}   
    
    protected List<? extends Good> getGoods() {
    	return this.getDomain().getGoods();
    }
}