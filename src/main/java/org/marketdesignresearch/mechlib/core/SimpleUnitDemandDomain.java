package org.marketdesignresearch.mechlib.core;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.core.bidder.UnitDemandBidder;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentation;
import org.marketdesignresearch.mechlib.metainfo.MetaInfo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public final class SimpleUnitDemandDomain implements Domain {

	@Getter
	private final List<? extends UnitDemandBidder> bidders;
	@Getter
	private final List<? extends Good> goods;

	private Allocation efficientAllocation;

	@Override
	public Allocation getEfficientAllocation() {
		if (efficientAllocation == null) {
			Map<UnitDemandBidder, BidderAllocation> allocationMap = new LinkedHashMap<>();
			Queue<UnitDemandBidder> bidderQueue = bidders.stream()
					.sorted((a, b) -> b.getValue().compareTo((a.getValue())))
					.collect(Collectors.toCollection(LinkedList::new));
			for (Good good : goods) {
				for (int i = 0; i < good.getQuantity() && !bidderQueue.isEmpty(); i++) {
					UnitDemandBidder winner = bidderQueue.poll();
					allocationMap.put(winner,
							new BidderAllocation(winner.getValue(), Bundle.of(good), new HashSet<>()));
				}
			}
			efficientAllocation = new Allocation(allocationMap, new BundleExactValueBids(), new MetaInfo());
		}
		return efficientAllocation;
	}

	@Override
	public String getName() {
		return "Unit Demand Domain";
	}

	// region instrumentation
	@Override
	public void setMipInstrumentation(MipInstrumentation mipInstrumentation) {
		// No MIP involved
		getBidders().forEach(bidder -> bidder.setMipInstrumentation(mipInstrumentation));
	}
	// endregion

}
