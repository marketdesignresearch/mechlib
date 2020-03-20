package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr.kernels;

import java.util.HashMap;
import java.util.HashSet;
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

public class WinnerDeterminationTranslationInvariantKernel extends WinnerDeterminationWithExcludedBundles{
	/*
	 * Doesn't work with multiple units!
	 */

    private KernelGaussian kernel;
    
    public WinnerDeterminationTranslationInvariantKernel(Domain domain, ElicitationEconomy economy,
    		BundleExactValueBids supportVectorsPerBidder,
			Map<Bidder, Set<Bundle>> excludedBids,
			KernelGaussian kernelGaussian) {
    	super(domain,economy,supportVectorsPerBidder,excludedBids);
		this.kernel = kernelGaussian;
	}

    @Override
	protected IMIP getSpecificMIP() {
    	
    	Map<UUID,Map<Bundle, Map<Good, Variable>>> bidderSVDiffVariables = new HashMap<>();
    	
    	MIPWrapper mipWrapper = MIPWrapper.makeNewMaxMIP();	    		      	
    	
    	for (UUID b : this.getEconomy().getBidders()){
    		bidderGoodVariables.put(b, new HashMap<>()); 
    		bidderSVDiffVariables.put(b,new HashMap<>());
    		
			//Insert variables, one per each good     		
			for (Good good : this.getDomain().getGoods()){
				bidderGoodVariables.get(b).put(good, mipWrapper.makeNewBooleanVar(b.toString()+" Good "+good.toString()));
			}
   			  			
			for (BundleExactValuePair bv : this.getSupportVectors().getBid(this.getBidder(b)).getBundleBids()){
				bidderSVDiffVariables.get(b).put(bv.getBundle(), new HashMap<>());
				Constraint cSet = mipWrapper.beginNewEQConstraint(1+bv.getBundle().getTotalAmount());
				Constraint cSize = mipWrapper.beginNewEQConstraint(1);
				
				int goodIdx = 0;
				for (Good good : this.getDomain().getGoods()){
					bidderSVDiffVariables.get(b).get(bv.getBundle()).put(good, mipWrapper.makeNewBooleanVar(b.toString()+" "+bv.toString()+" Diff "+good.toString()));
					mipWrapper.addObjectiveTerm(bv.getAmount().doubleValue()*kernel.getValueGivenDifference(goodIdx),bidderSVDiffVariables.get(b).get(bv.getBundle()).get(good));
					cSet.addTerm(goodIdx+1, bidderSVDiffVariables.get(b).get(bv.getBundle()).get(good));
					cSize.addTerm(1, bidderSVDiffVariables.get(b).get(bv.getBundle()).get(good));
					goodIdx++;
				}
				
				Set<Good> complementSet = new HashSet<>();
				//TODO: Implement multiple units!
				for (Good good : this.getDomain().getGoods()){
					if (bv.getBundle().countGood(good)==0) complementSet.add(good);
					if (bv.getBundle().countGood(good)>1) System.out.println("I am ignoring multiple units!");
				}
				for (Good good : this.getDomain().getGoods()){
					if (bv.getBundle().countGood(good)==1) cSet.addTerm(+1.0, bidderGoodVariables.get(b).get(good));
					if (bv.getBundle().countGood(good)>1) {System.out.println("I am ignoring multiple units!");cSet.addTerm(+1.0, bidderGoodVariables.get(b).get(good));}
				}
				for (Good good : complementSet) cSet.addTerm(-1.0, bidderGoodVariables.get(b).get(good));
				
				mipWrapper.endConstraint(cSet);
				mipWrapper.endConstraint(cSize);
			}
    	}
    	    	
    	for (Good good : this.getDomain().getGoods()){
    		Constraint c = mipWrapper.beginNewLEQConstraint(1);
    		for (UUID b : this.getEconomy().getBidders()){
    			c.addTerm(1, bidderGoodVariables.get(b).get(good));
    		}
    		mipWrapper.endConstraint(c);
    	}
    		
    	return mipWrapper;
    } 
}
