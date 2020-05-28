package org.marketdesignresearch.mechlib.outcomerules.ccg;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.Payment;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentation;
import org.marketdesignresearch.mechlib.metainfo.MetaInfo;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRule;
import org.marketdesignresearch.mechlib.outcomerules.ccg.blockingallocation.BlockingAllocation;
import org.marketdesignresearch.mechlib.outcomerules.ccg.blockingallocation.BlockingAllocationFinder;
import org.marketdesignresearch.mechlib.outcomerules.ccg.constraintgeneration.ConstraintGenerationAlgorithm;
import org.marketdesignresearch.mechlib.outcomerules.ccg.constraintgeneration.ConstraintGenerator;
import org.marketdesignresearch.mechlib.outcomerules.ccg.paymentrules.CorePaymentRule;
import org.marketdesignresearch.mechlib.utils.PrecisionUtils;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CCGOutcomeRule implements OutcomeRule {
    private Outcome result = null;
    private final BundleValueBids<?> bids;
    private final CorePaymentRule paymentRule;
    private final Allocation allocation;
    private final BlockingAllocationFinder blockingCoalitionFactory;
    private final Set<ConstraintGenerationAlgorithm> cgAlgorithms;

    public CCGOutcomeRule(BundleValueBids<?> bids, Allocation allocation, BlockingAllocationFinder blockingCoalitionFactory, CorePaymentRule paymentRule,
                          ConstraintGenerationAlgorithm... cgAlgorithms) {
        this(bids, allocation, paymentRule, blockingCoalitionFactory, EnumSet.copyOf(Arrays.asList(cgAlgorithms)));
    }

    public CCGOutcomeRule(BundleValueBids<?> bids, Allocation allocation, CorePaymentRule paymentRule, BlockingAllocationFinder blockingCoalitionFactory,
                          Set<ConstraintGenerationAlgorithm> cgAlgorithms) {
        this.bids = bids;
        this.allocation = allocation;
        this.paymentRule = paymentRule;
        this.blockingCoalitionFactory = Objects.requireNonNull(blockingCoalitionFactory);
        this.cgAlgorithms = cgAlgorithms;
    }

    @Override
    public Outcome getOutcome() {
        if (result == null) {
            result = calculateCCGPrices(allocation);
        }
        return result;
    }

    private Outcome calculateCCGPrices(Allocation allocation) {
        long start = System.currentTimeMillis();
        MetaInfo metaInfo = new MetaInfo();
        Payment lastPayment = paymentRule.getPayment();
        ConstraintGenerator constraintGenerator = ConstraintGenerationAlgorithm.getInstance(cgAlgorithms, bids, new Outcome(lastPayment, allocation), paymentRule);
        BigDecimal totalWinnersPayments = lastPayment.getTotalPayments();
        BigDecimal lastBlockingAllocationValue = null;
        BlockingAllocation blockingAllocation = null;
        Outcome lastResult = null;
        BigDecimal comparisonPrecision = PrecisionUtils.EPSILON.scaleByPowerOfTen(1);
        do {

            if (blockingAllocation != null) {
                // Omit first run
                log.debug("adding constraints");
                for (Allocation allo : blockingAllocation) {
                    constraintGenerator.addConstraint(allo, lastResult);

                }
                log.debug("constraints added");
                log.debug("minimizing payment");

                lastPayment = paymentRule.getPayment();
                log.debug("payment minimized");

                totalWinnersPayments = lastPayment.getTotalPayments();
            }
            lastResult = new Outcome(lastPayment, allocation);
            log.debug("Total winners payments {}", totalWinnersPayments);
            blockingAllocation = blockingCoalitionFactory.findBlockingAllocation(bids, lastResult);
            metaInfo = metaInfo.join(blockingAllocation.getMostBlockingAllocation().getMetaInfo());
            
            if(lastBlockingAllocationValue != null) {
            	if(PrecisionUtils.fuzzyEquals(lastBlockingAllocationValue, blockingAllocation.getMostBlockingAllocation().getTotalAllocationValue(), PrecisionUtils.EPSILON.scaleByPowerOfTen(1))) {
            		comparisonPrecision = comparisonPrecision.scaleByPowerOfTen(1);
            		if(comparisonPrecision.compareTo(BigDecimal.valueOf(0.1))>0) 
            			throw new IllegalStateException("Precision of comparison of winner payments and block allocation decreases below 0.1 - This might be due to numerical issues. Please check your MIPs");
            	} else {
            		comparisonPrecision = PrecisionUtils.EPSILON.scaleByPowerOfTen(1);
            	}
            }
            
            lastBlockingAllocationValue = blockingAllocation.getMostBlockingAllocation().getTotalAllocationValue();
            log.debug("Blocking coalition found with value {}", blockingAllocation.getMostBlockingAllocation().getTotalAllocationValue());
        } while (PrecisionUtils.fuzzyCompare(blockingAllocation.getMostBlockingAllocation().getTotalAllocationValue(), totalWinnersPayments, comparisonPrecision
                ) > 0
                && !blockingAllocation.getMostBlockingAllocation().getWinners().equals(allocation.getWinners()));

        long end = System.currentTimeMillis();
        log.debug("Finished CCG running time: {}ms", end - start);
        metaInfo.setJavaRuntime(end - start);
        metaInfo = metaInfo.join(lastPayment.getMetaInfo());
        Payment payment = new Payment(lastPayment.getPaymentMap(), metaInfo);

        return new Outcome(payment, allocation);
    }

    @Override
    public Allocation getAllocation() {
        return allocation;
    }

    // region instrumentation
    @Getter @Setter
    private MipInstrumentation mipInstrumentation = MipInstrumentation.NO_OP;
    // endregion
}