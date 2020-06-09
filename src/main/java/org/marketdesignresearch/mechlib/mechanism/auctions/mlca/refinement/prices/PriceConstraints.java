package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.refinement.prices;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValuePair;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PriceConstraints {

	private final Map<UUID, Map<Bundle, BigDecimal>> rightHandSides;
	
	public PriceConstraints(List<UUID> bidders) {
		rightHandSides = bidders.stream().collect(Collectors.toMap(b->b, b-> new HashMap<Bundle, BigDecimal>()));
	}
	
	public PriceConstraints(Domain domain, List<UUID> bidders, BundleExactValueBids bids, Allocation allocation, PriceConstraints baseline) {
		// TODO check update
		this.rightHandSides = new LinkedHashMap<>();
		
		for(Bidder bidder : bidders.stream().map(id -> domain.getBidders().stream().filter(b -> b.getId().equals(id)).findAny().orElseThrow()).collect(Collectors.toCollection(LinkedHashSet::new))) {
			this.rightHandSides.put(bidder.getId(), new LinkedHashMap<>());
			
			BundleExactValueBid values = bids.getBid(bidder);
			
			Bundle allocated = allocation.allocationOf(bidder).getBundle();
			BigDecimal allocatedValue = values.getBidForBundle(allocated).getAmount();
			
			for(BundleExactValuePair bid : values.getBundleBids()) {
				if(!bid.getBundle().equals(allocated)) {
					BigDecimal rightHandSide = allocatedValue.subtract(values.getBidForBundle(bid.getBundle()).getAmount());
					if(baseline.getConstrainedBids(bidder.getId()).contains(bid.getBundle()))
						rightHandSide = rightHandSide.min(baseline.getRightHandSide(bidder.getId(), bid.getBundle()));
					
					this.rightHandSides.get(bidder.getId()).put(bid.getBundle(), rightHandSide);
				}
			}
		}
	}
	
	public BigDecimal getRightHandSide(UUID bidder, Bundle bundle) {
		return this.rightHandSides.get(bidder).get(bundle);
	}
	
	public Set<Bundle> getConstrainedBids(UUID bidder) {
		return this.rightHandSides.get(bidder).keySet();
	}
}
