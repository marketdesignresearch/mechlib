package org.marketdesignresearch.mechlib.mechanisms.ccg;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.Payment;
import org.marketdesignresearch.mechlib.mechanisms.OutputRule;
import org.marketdesignresearch.mechlib.mechanisms.MechanismResult;
import org.marketdesignresearch.mechlib.mechanisms.MetaInfo;
import org.marketdesignresearch.mechlib.mechanisms.ccg.blockingallocation.BlockingAllocation;
import org.marketdesignresearch.mechlib.mechanisms.ccg.blockingallocation.BlockingAllocationFinder;
import org.marketdesignresearch.mechlib.mechanisms.ccg.constraintgeneration.ConstraintGenerationAlgorithm;
import org.marketdesignresearch.mechlib.mechanisms.ccg.constraintgeneration.ConstraintGenerator;
import org.marketdesignresearch.mechlib.mechanisms.ccg.paymentrules.CorePaymentRule;
import org.marketdesignresearch.mechlib.utils.PrecisionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

public class CCGMechanism implements OutputRule {
    private static final Logger LOGGER = LoggerFactory.getLogger(CCGMechanism.class);
    private MechanismResult result = null;
    private final Bids bids;
    private final CorePaymentRule paymentRule;
    private final Allocation allocation;
    private final BlockingAllocationFinder blockingCoalitionFactory;
    private final Set<ConstraintGenerationAlgorithm> cgAlgorithms;

    public CCGMechanism(Bids bids, Allocation allocation, BlockingAllocationFinder blockingCoalitionFactory, CorePaymentRule paymentRule,
                        ConstraintGenerationAlgorithm... cgAlgorithms) {
        this(bids, allocation, paymentRule, blockingCoalitionFactory, EnumSet.copyOf(Arrays.asList(cgAlgorithms)));
    }

    public CCGMechanism(Bids bids, Allocation allocation, CorePaymentRule paymentRule, BlockingAllocationFinder blockingCoalitionFactory,
                        Set<ConstraintGenerationAlgorithm> cgAlgorithms) {
        this.bids = bids;
        this.allocation = allocation;
        this.paymentRule = paymentRule;
        this.blockingCoalitionFactory = Objects.requireNonNull(blockingCoalitionFactory);
        this.cgAlgorithms = cgAlgorithms;
    }

    @Override
    public MechanismResult getMechanismResult() {
        if (result == null) {
            result = calculateCCGPrices(allocation);
        }
        return result;
    }

    private MechanismResult calculateCCGPrices(Allocation allocation) {
        long start = System.currentTimeMillis();
        MetaInfo metaInfo = new MetaInfo();
        Payment lastPayment = paymentRule.getPayment();
        ConstraintGenerator constraintGenerator = ConstraintGenerationAlgorithm.getInstance(cgAlgorithms, bids, new MechanismResult(lastPayment, allocation), paymentRule);
        BigDecimal totalWinnersPayments = lastPayment.getTotalPayments();
        BlockingAllocation blockingAllocation = null;
        MechanismResult lastResult = null;
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
            lastResult = new MechanismResult(lastPayment, allocation);
            LOGGER.debug("Total winners payments {}", totalWinnersPayments);
            blockingAllocation = blockingCoalitionFactory.findBlockingAllocation(bids, lastResult);
            metaInfo = metaInfo.join(blockingAllocation.getMostBlockingAllocation().getMetaInfo());
            LOGGER.debug("Blocking coalition found with value {}", blockingAllocation.getMostBlockingAllocation().getTotalAllocationValue());
        } while (PrecisionUtils.fuzzyCompare(blockingAllocation.getMostBlockingAllocation().getTotalAllocationValue(), totalWinnersPayments,
                PrecisionUtils.EPSILON.scaleByPowerOfTen(1)) > 0
                && !blockingAllocation.getMostBlockingAllocation().getWinners().equals(allocation.getWinners()));

        long end = System.currentTimeMillis();
        LOGGER.debug("Finished CCGMechanism running time: {}ms", end - start);
        metaInfo.setJavaRuntime(end - start);
        metaInfo = metaInfo.join(lastPayment.getMetaInfo());
        Payment payment = new Payment(lastPayment.getPaymentMap(), metaInfo);

        return new MechanismResult(payment, allocation);
    }

    @Override
    public Allocation getAllocation() {
        return allocation;
    }
}