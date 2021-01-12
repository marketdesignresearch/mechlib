package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr.kernels;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.BidderAllocation;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.BundleEntry;
import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.allocationlimits.AllocationLimit;
import org.marketdesignresearch.mechlib.core.allocationlimits.AllocationLimitConstraint;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentation.MipPurpose;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.ElicitationEconomy;
import org.marketdesignresearch.mechlib.metainfo.MetaInfo;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;

import com.google.common.collect.ImmutableMap;

import edu.harvard.econcs.jopt.solver.IMIP;
import edu.harvard.econcs.jopt.solver.ISolution;
import edu.harvard.econcs.jopt.solver.SolveParam;
import edu.harvard.econcs.jopt.solver.mip.CompareType;
import edu.harvard.econcs.jopt.solver.mip.Constraint;
import edu.harvard.econcs.jopt.solver.mip.Variable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Kernel WDP that excludes certain allocation (based on allocated bundles) from the feasible set of allocations.
 * 
 * @author Gianluca Brero
 * @author Manuel Beyeler
 */
@RequiredArgsConstructor
public abstract class WinnerDeterminationWithExcludedBundles extends WinnerDetermination {

	@Override
	protected Allocation solveWinnerDetermination() {
		try {
			return super.solveWinnerDetermination();
		} catch(RuntimeException e) {
			this.getMIP().setSolveParam(SolveParam.OPTIMALITY_TARGET, 3);
			try {
				return super.solveWinnerDetermination();
			} catch (RuntimeException e2) {
				this.getMIP().setSolveParam(SolveParam.OPTIMALITY_TARGET, 0);
				this.getMIP().setSolveParam(SolveParam.LP_OPTIMIZATION_ALG, 4);
				return super.solveWinnerDetermination();
			}
		}
	}

	private static final double DEFAULT_EPSILON = 0d;
	
	@Getter(AccessLevel.PACKAGE)
	protected Map<UUID, Map<Good, Variable>> bidderGoodVariables = new LinkedHashMap<>();

	@Getter
	private final Domain domain;
	@Getter
	private final ElicitationEconomy economy;
	@Getter
	private final BundleExactValueBids supportVectors;
	@Getter
	private final Map<Bidder, Set<Bundle>> excludedBundles;
	@Getter
	private final boolean genericSetting;

	private IMIP winnerDeterminationProgram = null;

	public WinnerDeterminationWithExcludedBundles(Domain domain, ElicitationEconomy economy,
			BundleExactValueBids supportVectors, Map<Bidder, Set<Bundle>> excludedBundles, double timelimit) {
		this.domain = domain;
		this.economy = economy;
		this.supportVectors = supportVectors;
		this.excludedBundles = excludedBundles;
		this.genericSetting = this.getDomain().getGoods().stream().map(g -> g.getQuantity() > 1)
				.reduce(Boolean::logicalOr).get();
		this.setPurpose(MipPurpose.KERNEL_WINNERDETERMINATION.name());
		this.setTimeLimit(timelimit);
		this.setEpsilon(DEFAULT_EPSILON);
	}

	protected Bidder getBidder(UUID id) {
		return this.domain.getBidders().stream().filter(b -> b.getId().equals(id)).findFirst()
				.orElseThrow(NoSuchElementException::new);
	}

	protected Allocation adaptMIPResult(ISolution mipResult) {

		ImmutableMap.Builder<Bidder, BidderAllocation> trades = ImmutableMap.builder();

		for (UUID bidder : this.getEconomy().getBidders()) {
			Set<BundleEntry> entries = new LinkedHashSet<>();
			for (Good good : this.getGoods()) {
				double value = mipResult.getValue(bidderGoodVariables.get(bidder).get(good));
				if (value >= 1 - 1e-3 && value <= 1 + 1e-3)
					entries.add(new BundleEntry(good,
							(int) Math.floor(mipResult.getValue(bidderGoodVariables.get(bidder).get(good)) + 0.5)));
			}
			trades.put(this.getBidder(bidder),
					new BidderAllocation(BigDecimal.ZERO, new Bundle(entries), Collections.emptySet()));
		}

		MetaInfo metaInfo = new MetaInfo();
		metaInfo.setNumberOfMIPs(1);
		metaInfo.setMipSolveTime(mipResult.getSolveTime());
		// if (TimeUnit.MILLISECONDS.toSeconds(mipResult.getSolveTime()) >=
		// cplexTimeLimit) metainfo.setHitTimeLimit(true);
		return new Allocation(BigDecimal.valueOf(mipResult.getObjectiveValue()), trades.build(),
				new BundleExactValueBids(), metaInfo);
	}

	private IMIP createWinnerDeterminationProgram() {
		IMIP mip = this.createKernelSpecificWinnerDeterminationProgram();

		for (Map.Entry<Bidder, Set<Bundle>> bidderEntry : this.excludedBundles.entrySet()) {
			for (Bundle bundle : bidderEntry.getValue()) {
				Constraint intCut = new Constraint(CompareType.LEQ, bundle.getTotalAmount() - 1 + 1e-8);
				mip.add(intCut);
				for (Good good : this.getGoods()) {
					if (bundle.contains(good))
						intCut.addTerm(1, this.bidderGoodVariables.get(bidderEntry.getKey().getId()).get(good));
					else
						intCut.addTerm(-1, this.bidderGoodVariables.get(bidderEntry.getKey().getId()).get(good));
				}
			}
		}

		// apply AllocationLimits
		for (UUID bUUID : this.getEconomy().getBidders()) {
			AllocationLimit limit = this.getBidder(bUUID).getAllocationLimit();
			for (AllocationLimitConstraint alc : limit.getConstraints()) {
				mip.add(alc.createCPLEXConstraint(this.bidderGoodVariables.get(bUUID)));
			}
			limit.getAdditionalVariables().forEach(mip::add);
		}

		return mip;
	}

	@Override
	protected IMIP getMIP() {
		if (this.winnerDeterminationProgram == null)
			this.winnerDeterminationProgram = this.createWinnerDeterminationProgram();
		return this.winnerDeterminationProgram;
	}

	protected abstract IMIP createKernelSpecificWinnerDeterminationProgram();

	@Override
	public WinnerDetermination join(WinnerDetermination other) {
		throw new UnsupportedOperationException();
	}

	protected List<? extends Good> getGoods() {
		return this.getDomain().getGoods();
	}
}