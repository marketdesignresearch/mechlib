package org.marketdesignresearch.mechlib.instrumentation;

import edu.harvard.econcs.jopt.solver.IMIP;
import edu.harvard.econcs.jopt.solver.IMIPResult;
import org.marketdesignresearch.mechlib.core.Allocation;

import java.util.List;

public class MipInstrumentation {
    public void postMIP(MipPurpose mipPurpose, IMIP mip, IMIPResult result, Allocation bestAllocation, List<Allocation> poolAllocations) {};

    public enum MipPurpose {
        ALLOCATION,
        PAYMENT,
        DEMAND_QUERY
    }
}
