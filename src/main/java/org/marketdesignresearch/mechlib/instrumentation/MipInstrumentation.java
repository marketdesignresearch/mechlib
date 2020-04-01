package org.marketdesignresearch.mechlib.instrumentation;

import edu.harvard.econcs.jopt.solver.IMIP;
import edu.harvard.econcs.jopt.solver.IMIPResult;
import lombok.EqualsAndHashCode;
import org.marketdesignresearch.mechlib.core.Allocation;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode
public class MipInstrumentation {

    public static MipInstrumentation NO_OP = new MipInstrumentation();

    protected MipInstrumentation() {}
    
    public void preMIP(MipPurpose mipPurpose, IMIP mip) {}

    public void postMIP(MipPurpose mipPurpose, IMIP mip, IMIPResult result, Allocation bestAllocation, List<Allocation> poolAllocations) {}

    public void postMIP(MipPurpose mipPurpose, IMIP mip, IMIPResult result) {
        this.postMIP(mipPurpose, mip, result, Allocation.EMPTY_ALLOCATION, new ArrayList<>());
    }

    public enum MipPurpose {
        ALLOCATION,
        PAYMENT,
        DEMAND_QUERY
    }
}
