package org.marketdesignresearch.mechlib.instrumentation;

import edu.harvard.econcs.jopt.solver.IMIP;
import edu.harvard.econcs.jopt.solver.IMIPResult;
import edu.harvard.econcs.jopt.solver.mip.PoolSolution;
import lombok.extern.slf4j.Slf4j;
import org.marketdesignresearch.mechlib.core.Allocation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;

@Slf4j
public class MipLoggingInstrumentation extends MipInstrumentation {
    @Override
    public void postMIP(MipPurpose mipPurpose, IMIP mip, IMIPResult result, Allocation bestAllocation, List<Allocation> poolAllocations) {
        log.info("MIP Purpose: {}", mipPurpose);
        log.info("MIP -> # Variables: {}", mip.getNumVars());
        log.info("MIP -> # Constraints: {}", mip.getNumConstraints());
        log.info("MIP Result -> Objective value: {}", result.getObjectiveValue());
        log.info("MIP Result -> Relative gap: {}%", BigDecimal.valueOf(result.getRelativeGap() * 100).setScale(4, RoundingMode.HALF_UP));
        log.info("MIP Result -> Absolute gap: {}", result.getAbsoluteGap());
        log.info("MIP Result -> Solve time: {}ms", result.getSolveTime());
        Queue<PoolSolution> poolSolutions = result.getPoolSolutions();
        log.info("# Pool Solutions: {}", poolSolutions.size());
        if (poolSolutions.size() > 0) {
            PoolSolution worst = poolSolutions.stream().min(Comparator.comparingDouble(PoolSolution::getObjectiveValue)).orElseThrow(NoSuchElementException::new);
            log.info("Worst Pool Solution -> Objective value: {}", worst.getObjectiveValue());
            log.info("Worst Pool Solution -> Relative pool gap: {}%", BigDecimal.valueOf(worst.getPoolRelativeGap() * 100).setScale(4, RoundingMode.HALF_UP));
            log.info("Worst Pool Solution -> Absolute pool gap: {}", worst.getPoolAbsoluteGap());
        }
        log.info("Best Allocation -> Total value: {}", bestAllocation.getTotalAllocationValue().setScale(4, RoundingMode.HALF_UP));
        log.info("# Pool Allocations: {}", poolAllocations.size());
    }
}
