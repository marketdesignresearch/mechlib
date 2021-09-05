package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.bidder.random.BidderRandom;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRoundBuilder;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.impl.DefaultBoundValueQueryWithMRPARRefinementInteraction;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.ElicitationEconomy;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.MachineLearningComponent;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.refinement.BidderRefinementRoundInfo;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.refinement.BidderRefinementRoundInfoCreator;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.refinement.EfficiencyGuaranteeEfficiencyInfoCreator;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.refinement.EfficiencyInfoCreator;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.refinement.MRPARRefinementRoundInfoCreator;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.refinement.MRPAR_DIAR_RefinementRoundInfoCreator;

import lombok.Getter;
import lombok.Setter;

/**
 * The second phase of iMLCA (Beyeler et. al. 2021)
 * 
 * @author Manuel Beyeler
 */
public class BoundMLQueryWithMRPARPhase extends MLQueryPhase<BundleBoundValueBids> {

	private static final boolean DEFAULT_REFINE_MARGINAL_ECONOMIES = false;
	private static final boolean DEFAULT_INTERMEDIATE_REFINEMENTS = false;

	private final boolean refineMarginalEconomies;
	private final boolean intermediateRefinements;

	@Getter
	@Setter
	private BidderRefinementRoundInfoCreator mlRoundRefinementInfoCreator = new MRPARRefinementRoundInfoCreator();

	@Getter
	@Setter
	private BidderRefinementRoundInfoCreator intermediateRefinementInfoCreator = new MRPAR_DIAR_RefinementRoundInfoCreator();

	@Getter
	@Setter
	private EfficiencyInfoCreator efficiencyInfoCreator = new EfficiencyGuaranteeEfficiencyInfoCreator();

	@Getter
	private List<ElicitationEconomy> refinementEconomies;

	/**
	 * @param mlComponent             the ml component used to generate queries
	 * @param maxQueries              the maximal number of queries per bidder
	 *                                (Q^max in Beyeler et. al. 2021)
	 * @param numberOfMarginalQueries the number of marginal queries per bidder and
	 *                                round (equals Q^round -1 in Beyeler et. al.
	 *                                2021, note that Q^round also includes a query
	 *                                for the main economy.
	 * @param timeLimit               the time limit to solve the ML WDP
	 */
	public BoundMLQueryWithMRPARPhase(MachineLearningComponent<BundleBoundValueBids> mlComponent, int maxQueries,
			int numberOfMarginalQueries, double timeLimit) {
		this(mlComponent, maxQueries, numberOfMarginalQueries, DEFAULT_REFINE_MARGINAL_ECONOMIES,
				DEFAULT_INTERMEDIATE_REFINEMENTS, timeLimit);
	}

	/**
	 * @param mlComponent             the ml component used to generate queries
	 * @param maxQueries              the maximal number of queries per bidder
	 *                                (Q^max in Beyeler et. al. 2021)
	 * @param numberOfMarginalQueries the number of marginal queries per bidder and
	 *                                round (equals Q^round -1 in Beyeler et. al.
	 *                                2021, note that Q^round also includes a query
	 *                                for the main economy.
	 * @param refineMarginalEconomies if set to true, generates prices with respect
	 *                                to marginal economies to achieve higher
	 *                                efficiency in the marginal economies (version
	 *                                not described in Beyeler et. al 2021)
	 * @param timeLimit               the time limit to solve the ML WDP
	 */
	public BoundMLQueryWithMRPARPhase(MachineLearningComponent<BundleBoundValueBids> mlComponent, int maxQueries,
			int numberOfMarginalQueries, boolean refineMarginalEconomies, double timeLimit) {
		this(mlComponent, maxQueries, numberOfMarginalQueries, refineMarginalEconomies,
				DEFAULT_INTERMEDIATE_REFINEMENTS, timeLimit);
	}

	/**
	 * @param mlComponent             the ml component used to generate queries
	 * @param maxQueries              the maximal number of queries per bidder
	 *                                (Q^max in Beyeler et. al. 2021)
	 * @param numberOfMarginalQueries the number of marginal queries per bidder and
	 *                                round (equals Q^round -1 in Beyeler et. al.
	 *                                2021, note that Q^round also includes a query
	 *                                for the main economy.
	 * @param refineMarginalEconomies if set to true, generates prices with respect
	 *                                to marginal economies to achieve higher
	 *                                efficiency in the marginal economies (version
	 *                                not described in Beyeler et. al 2021)
	 * @param intermediateRefinements if set to true, each round with ML based
	 *                                queries and MRPAR refinement will be followed
	 *                                by one refinement round with MRPAR and DIAR
	 *                                refinement (version not described in Beyeler
	 *                                et. al. 2021)
	 * @param timeLimit               the time limit to solve the ML WDP
	 */
	public BoundMLQueryWithMRPARPhase(MachineLearningComponent<BundleBoundValueBids> mlComponent, int maxQueries,
			int numberOfMarginalQueries, boolean refineMarginalEconomies, boolean intermediateRefinements,
			double timeLimit) {
		super(mlComponent, maxQueries, numberOfMarginalQueries);
		this.refineMarginalEconomies = refineMarginalEconomies;
		this.intermediateRefinements = intermediateRefinements;
		this.mlRoundRefinementInfoCreator.getPriceGenerator().setTimeLimit(timeLimit);
		this.intermediateRefinementInfoCreator.getPriceGenerator().setTimeLimit(timeLimit);
	}

	@Override
	public AuctionRoundBuilder<BundleBoundValueBids> createNextRoundBuilder(Auction<BundleBoundValueBids> auction) {
		if (this.refinementEconomies == null) {
			this.refinementEconomies = this.createAllRefinementEconomies(auction);
		}

		// check if requested and perform intermediate refinement
		if (intermediateRefinements
				&& BoundMLQueryWithMRPARAuctionRound.class.isAssignableFrom(auction.getLastRound().getClass())) {
			return new RefinementAuctionRoundBuilder(
					auction, refinementEconomies, this.getEfficiencyInfoCreator()
							.getEfficiencyInfo(auction.getLatestAggregatedBids(), this.refinementEconomies),
					this.getIntermediateRefinementInfoCreator());
		}

		return super.createNextRoundBuilder(auction);
	}

	@Override
	protected AuctionRoundBuilder<BundleBoundValueBids> createConcreteAuctionRoundBuilder(
			Auction<BundleBoundValueBids> auction, Map<Bidder, Set<Bundle>> restrictedBids,
			Map<UUID, List<ElicitationEconomy>> bidderMarginalsTemp) {

		Map<UUID, BidderRefinementRoundInfo> bidderRefinementInfos = this.getMlRoundRefinementInfoCreator()
				.createBidderRefinementRoundInfos(auction, BidderRandom.INSTANCE.getRandom(),
						this.getEfficiencyInfoCreator().getEfficiencyInfo(auction.getLatestAggregatedBids(),
								this.refinementEconomies));

		BundleBoundValueBids latestAggregatedBids = auction.getLatestAggregatedBids();

		return new BoundMLQueryWithMRPARAuctionRoundBuilder(auction,
				auction.getDomain().getBidders().stream()
						.collect(Collectors.toMap(Bidder::getId,
								b -> new DefaultBoundValueQueryWithMRPARRefinementInteraction(b.getId(), auction,
										bidderRefinementInfos.get(b.getId()).getAlphaAllocation().allocationOf(b)
												.getBundle(),
										bidderRefinementInfos.get(b.getId()).getPrices(),
										latestAggregatedBids.getBid(b), restrictedBids.get(b)),
								(e1, e2) -> e1, LinkedHashMap::new)),
				bidderMarginalsTemp, bidderRefinementInfos);
	}

	protected List<ElicitationEconomy> createAllRefinementEconomies(Auction<BundleBoundValueBids> auction) {
		List<ElicitationEconomy> elicitationEconomies = new ArrayList<>();
		elicitationEconomies.add(new ElicitationEconomy(auction.getDomain()));
		if (this.refineMarginalEconomies)
			auction.getDomain().getBidders()
					.forEach(bidder -> elicitationEconomies.add(new ElicitationEconomy(auction.getDomain(), bidder)));
		return elicitationEconomies;
	}

	@Override
	public String getType() {
		return "Bound ML Query with MRPAR Refinement Phase";
	}
}
