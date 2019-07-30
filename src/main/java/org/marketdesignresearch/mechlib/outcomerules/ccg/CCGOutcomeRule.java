package org.marketdesignresearch.mechlib.outcomerules.ccg;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.Payment;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRule;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.metainfo.MetaInfo;
import org.marketdesignresearch.mechlib.outcomerules.ccg.blockingallocation.BlockingAllocation;
import org.marketdesignresearch.mechlib.outcomerules.ccg.blockingallocation.BlockingAllocationFinder;
import org.marketdesignresearch.mechlib.outcomerules.ccg.constraintgeneration.ConstraintGenerationAlgorithm;
import org.marketdesignresearch.mechlib.outcomerules.ccg.constraintgeneration.ConstraintGenerator;
import org.marketdesignresearch.mechlib.outcomerules.ccg.paymentrules.CorePaymentRule;
import org.marketdesignresearch.mechlib.utils.PrecisionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

public class CCGOutcomeRule implements OutcomeRule {
    private static final Logger LOGGER = LoggerFactory.getLogger(CCGOutcomeRule.class);
    private Outcome result = null;
    private final Bids bids;
    private final CorePaymentRule paymentRule;
    private final Allocation allocation;
    private final BlockingAllocationFinder blockingCoalitionFactory;
    private final Set<ConstraintGenerationAlgorithm> cgAlgorithms;

    public CCGOutcomeRule(Bids bids, Allocation allocation, BlockingAllocationFinder blockingCoalitionFactory, CorePaymentRule paymentRule,
                          ConstraintGenerationAlgorithm... cgAlgorithms) {
        this(bids, allocation, paymentRule, blockingCoalitionFactory, EnumSet.copyOf(Arrays.asList(cgAlgorithms)));
    }

    public CCGOutcomeRule(Bids bids, Allocation allocation, CorePaymentRule paymentRule, BlockingAllocationFinder blockingCoalitionFactory,
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
        BlockingAllocation blockingAllocation = null;
        Outcome lastResult = null;
        do {

            if (blockingAllocation != null) {
                // Omit first run
                LOGGER.debug("adding constraints");
                for (Allocation allo : blockingAllocation) {
                    constraintGenerator.addConstraint(allo, lastResult);

                }
                LOGGER.debug("constraints added");
                LOGGER.debug("minimizing payment");

                lastPayment = paymentRule.getPayment();
                LOGGER.debug("payment minimized");

                totalWinnersPayments = lastPayment.getTotalPayments();
            }
            lastResult = new Outcome(lastPayment, allocation);
            LOGGER.debug("Total winners payments {}", totalWinnersPayments);
            blockingAllocation = blockingCoalitionFactory.findBlockingAllocation(bids, lastResult);
            metaInfo = metaInfo.join(blockingAllocation.getMostBlockingAllocation().getMetaInfo());
            LOGGER.debug("Blocking coalition found with value {}", blockingAllocation.getMostBlockingAllocation().getTotalAllocationValue());
        } while (PrecisionUtils.fuzzyCompare(blockingAllocation.getMostBlockingAllocation().getTotalAllocationValue(), totalWinnersPayments,
                PrecisionUtils.EPSILON.scaleByPowerOfTen(1)) > 0
                && !blockingAllocation.getMostBlockingAllocation().getWinners().equals(allocation.getWinners()));

        long end = System.currentTimeMillis();
        LOGGER.debug("Finished CCG running time: {}ms", end - start);
        metaInfo.setJavaRuntime(end - start);
        metaInfo = metaInfo.join(lastPayment.getMetaInfo());
        Payment payment = new Payment(lastPayment.getPaymentMap(), metaInfo);

        return new Outcome(payment, allocation);
    }

    @Override
    public Allocation getAllocation() {
        return allocation;
    }
}