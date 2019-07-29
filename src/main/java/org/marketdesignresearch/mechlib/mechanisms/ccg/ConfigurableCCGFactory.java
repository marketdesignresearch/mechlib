package org.marketdesignresearch.mechlib.mechanisms.ccg;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.Payment;
import org.marketdesignresearch.mechlib.mechanisms.MechanismResult;
import org.marketdesignresearch.mechlib.mechanisms.ccg.blockingallocation.BlockingAllocationFinder;
import org.marketdesignresearch.mechlib.mechanisms.ccg.constraintgeneration.ConstraintGenerationAlgorithm;
import org.marketdesignresearch.mechlib.mechanisms.ccg.paymentrules.*;
import org.marketdesignresearch.mechlib.mechanisms.ccg.referencepoint.ReferencePointFactory;
import org.marketdesignresearch.mechlib.winnerdetermination.XORWinnerDetermination;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ConfigurableCCGFactory implements CCGMechanismFactory, ParameterizableMechanismFactory {
    /**
     *
     */
    private final Set<ConstraintGenerationAlgorithm> algorithms;
    private MechanismResult fixedReferencePoint = null;

    private final List<NormFactory> normFactories;
    private final BlockingAllocationFinder blockingAllocationFinder;
    private final ReferencePointFactory rpFactory;

    public ConfigurableCCGFactory(BlockingAllocationFinder blockingAllocationFinder, ReferencePointFactory rpFactory, List<NormFactory> normFactories,
                                  Set<ConstraintGenerationAlgorithm> algorithms) {
        this.normFactories = normFactories;
        this.rpFactory = rpFactory;
        this.blockingAllocationFinder = Objects.requireNonNull(blockingAllocationFinder);
        this.algorithms = algorithms;
    }

    public ConfigurableCCGFactory(BlockingAllocationFinder blockingAllocationFinder, ReferencePointFactory rpFactory, List<NormFactory> corePaymentNorms,
                                  ConstraintGenerationAlgorithm algorithm, ConstraintGenerationAlgorithm... algorithms) {
        this(blockingAllocationFinder, rpFactory, corePaymentNorms, Sets.immutableEnumSet(algorithm, algorithms));
    }

    public ConfigurableCCGFactory(NormFactory secondaryNormFactory, BlockingAllocationFinder blockingAllocationFinder, ReferencePointFactory rpFactory,
                                  ConstraintGenerationAlgorithm algorithm, ConstraintGenerationAlgorithm... algorithms) {
        this(blockingAllocationFinder, rpFactory, ImmutableList.of(secondaryNormFactory), Sets.immutableEnumSet(algorithm, algorithms));
    }


    @Override
    public CCGMechanism getMechanism(Bids bids) {
        MechanismResult referencePoint = fixedReferencePoint;
        if (referencePoint == null) {
            Allocation allocation = new XORWinnerDetermination(bids).getAllocation();
            Payment payment = rpFactory.computeReferencePoint(bids, allocation);
            referencePoint = new MechanismResult(payment, allocation);
        }
        // Important to use supplier because otherwise vcgAuction is invoked
        return buildCCGAuction(bids, referencePoint);
    }

    protected CCGMechanism buildCCGAuction(Bids bids, MechanismResult referencePoint) {
        List<CorePaymentNorm> objectiveNorms = normFactories.stream().map(pnf -> pnf.getPaymentNorm(referencePoint)).collect(Collectors.toList());

        ParameterizedCorePaymentRule paymentRule = new ParameterizedCorePaymentRule(objectiveNorms);
        return new CCGMechanism(bids, referencePoint.getAllocation(), paymentRule, blockingAllocationFinder, algorithms);
    }

    @Override
    public void setReferencePoint(MechanismResult cachedReferencePoint) {
        fixedReferencePoint = cachedReferencePoint;
    }

    @Override
    public boolean enforceMRC() {
        if (normFactories.size() <= 1) {
            return false;
        } else {
            NormFactory firstNorm = normFactories.get(0);
            boolean l1Norm = firstNorm.getNorm() == Norm.MANHATTAN && firstNorm.getWeightsFactory() instanceof EqualWeightsFactory;
            boolean belowCore = firstNorm.getFixedPayments().map(p -> p.equals(Payment.ZERO)).orElse(false) || rpFactory.belowCore();
            return l1Norm && belowCore;
        }
    }

    @Override
    public String lubinParkesName() {
        int minPrimaryNorms = enforceMRC() ? 1 : 0;
        NormFactory primaryFactory = normFactories.get(minPrimaryNorms);
        String name = primaryFactory.getLubinParkesName(rpFactory);
        if (name.equals("L1RP") && normFactories.size() > minPrimaryNorms + 1) {
            NormFactory normFactory = normFactories.get(minPrimaryNorms + 1);
            name += normFactory.getLubinParkesName(rpFactory);
        }
        return name;
    }

    @Override
    public String tieBreaker() {
        return normFactories.get(normFactories.size() - 1).getLubinParkesName(rpFactory);
    }

    @Override
    public String getReferencePoint() {
        return rpFactory.getName();
    }

    @Override
    public String toString() {
        return "ConfigurableCCGFactory[mrc=" + enforceMRC() + " ,epsilon=" + getEpsilon() + "delta=" + getDelta() + ", lubinParkesName=" + lubinParkesName() + ", tiebreaker="
                + tieBreaker() + ", referencePoint=" + getReferencePoint() + " ,blockingCoalitionFinder=" + blockingAllocationFinder + "]";
    }

    @Override
    public BlockingAllocationFinder getBlockingAllocationFinder() {
        return blockingAllocationFinder;
    }

    @Override
    public BigDecimal getDelta() {
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getEpsilon() {
        return BigDecimal.ZERO;
    }

    @Override
    public boolean referencePointBelowCore() {
        return rpFactory.belowCore();
    }

}
