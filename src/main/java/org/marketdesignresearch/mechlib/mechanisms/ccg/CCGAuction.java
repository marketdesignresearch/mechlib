package org.marketdesignresearch.mechlib.mechanisms.ccg;

import org.marketdesignresearch.mechlib.domain.Allocation;
import org.marketdesignresearch.mechlib.domain.AuctionInstance;
import org.marketdesignresearch.mechlib.domain.Payment;
import org.marketdesignresearch.mechlib.mechanisms.AuctionMechanism;
import org.marketdesignresearch.mechlib.mechanisms.AuctionResult;
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

public class CCGAuction implements AuctionMechanism {
    private static final Logger LOGGER = LoggerFactory.getLogger(CCGAuction.class);
    private AuctionResult result = null;
    private final AuctionInstance auctionInstance;
    private final CorePaymentRule paymentRule;
    private final Allocation allocation;
    private final BlockingAllocationFinder blockingCoalitionFactory;
    private final Set<ConstraintGenerationAlgorithm> cgAlgorithms;

    public CCGAuction(AuctionInstance auctionInstance, Allocation allocation, BlockingAllocationFinder blockingCoalitionFactory, CorePaymentRule paymentRule,
                      ConstraintGenerationAlgorithm... cgAlgorithms) {
        this(auctionInstance, allocation, paymentRule, blockingCoalitionFactory, EnumSet.copyOf(Arrays.asList(cgAlgorithms)));
    }

    public CCGAuction(AuctionInstance auctionInstance, Allocation allocation, CorePaymentRule paymentRule, BlockingAllocationFinder blockingCoalitionFactory,
                      Set<ConstraintGenerationAlgorithm> cgAlgorithms) {
        this.auctionInstance = auctionInstance;
        this.allocation = allocation;
        this.paymentRule = paymentRule;
        this.blockingCoalitionFactory = Objects.requireNonNull(blockingCoalitionFactory);
        this.cgAlgorithms = cgAlgorithms;
    }

    @Override
    public AuctionResult getAuctionResult() {
        if (result == null) {
            result = calculateCCGPrices(allocation);
        }
        return result;
    }

    private AuctionResult calculateCCGPrices(Allocation allocation) {
        long start = System.currentTimeMillis();
        MetaInfo metaInfo = new MetaInfo();
        Payment lastPayment = paymentRule.getPayment();
        ConstraintGenerator constraintGenerator = ConstraintGenerationAlgorithm.getInstance(cgAlgorithms, auctionInstance, new AuctionResult(lastPayment, allocation), paymentRule);
        BigDecimal totalWinnersPayments = lastPayment.getTotalPayments();
        BlockingAllocation blockingAllocation = null;
        AuctionResult lastResult = null;
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
            lastResult = new AuctionResult(lastPayment, allocation);
            LOGGER.debug("Total winners payments {}", totalWinnersPayments);
            blockingAllocation = blockingCoalitionFactory.findBlockingAllocation(auctionInstance, lastResult);
            metaInfo = metaInfo.join(blockingAllocation.getMostBlockingAllocation().getMetaInfo());
            LOGGER.debug("Blocking coalition found with value {}", blockingAllocation.getMostBlockingAllocation().getTotalAllocationValue());
        } while (PrecisionUtils.fuzzyCompare(blockingAllocation.getMostBlockingAllocation().getTotalAllocationValue(), totalWinnersPayments,
                PrecisionUtils.EPSILON.scaleByPowerOfTen(1)) > 0
                && !blockingAllocation.getMostBlockingAllocation().getWinners().equals(allocation.getWinners()));

        long end = System.currentTimeMillis();
        LOGGER.debug("Finished CCGAuction running time: {}ms", end - start);
        metaInfo.setJavaRuntime(end - start);
        metaInfo = metaInfo.join(lastPayment.getMetaInfo());
        Payment payment = new Payment(lastPayment.getPaymentMap(), metaInfo);

        return new AuctionResult(payment, allocation);
    }

    public AuctionInstance getAuction() {
        return auctionInstance;
    }

    @Override
    public Allocation getAllocation() {
        return allocation;
    }
}