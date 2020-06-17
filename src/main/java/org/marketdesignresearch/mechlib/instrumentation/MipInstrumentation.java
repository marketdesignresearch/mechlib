package org.marketdesignresearch.mechlib.instrumentation;

import java.util.ArrayList;
import java.util.List;

import org.marketdesignresearch.mechlib.core.Allocation;

import edu.harvard.econcs.jopt.solver.IMIP;
import edu.harvard.econcs.jopt.solver.IMIPResult;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class MipInstrumentation {
	
    public static MipInstrumentation NO_OP = new MipInstrumentation();

    protected MipInstrumentation() {}
    
    public void preMIP(String mipPurpose, IMIP mip) {}

    public void postMIP(String mipPurpose, IMIP mip, IMIPResult result, Allocation bestAllocation, List<Allocation> poolAllocations) {}

    public void postMIP(String mipPurpose, IMIP mip, IMIPResult result) {
        this.postMIP(mipPurpose, mip, result, Allocation.EMPTY_ALLOCATION, new ArrayList<>());
    }

    public enum MipPurpose {
        ALLOCATION,
        PAYMENT,
        DEMAND_QUERY,
        SUPPORT_VECTOR,
        KERNEL_WINNERDETERMINATION,
        REFINEMENT_PRICES
    }
}
