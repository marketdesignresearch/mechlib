package org.marketdesignresearch.mechlib.outcomerules;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.BidderAllocation;
import org.marketdesignresearch.mechlib.core.BidderPayment;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.Payment;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentation;
import org.marketdesignresearch.mechlib.outcomerules.ccg.constraintgeneration.PotentialCoalition;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OutcomeRuleScaler implements OutcomeRule{
	private final BigDecimal scale;
	private final BundleValueBids<?> originalBids;
	private final OutcomeRule base;
	
	@Override
	public void setMipInstrumentation(MipInstrumentation mipInstrumentation) {
		this.base.setMipInstrumentation(mipInstrumentation);	
	}
	@Override
	public Outcome getOutcome() {
		Outcome originalOutome = this.base.getOutcome();
		// create Payment with original values
		Payment newPayment = new Payment(originalOutome.getPayment().getPaymentMap().entrySet().stream()
					.collect(Collectors.toMap(Map.Entry::getKey, 
							e->new BidderPayment(
									e.getValue().getAmount().divide(this.scale,RoundingMode.HALF_UP))))
					,originalOutome.getPayment().getMetaInfo());
		
		// create Allocation with original values
		Map<Bidder,BidderAllocation> tradesMap = new LinkedHashMap<>();
		for(Map.Entry<Bidder,BidderAllocation> e :originalOutome.getAllocation().getTradesMap().entrySet()) {
			BigDecimal value = e.getValue().getAcceptedBids().stream()
					.map(b -> originalBids.getBid(e.getKey()).getBidForBundle(b.getBundle()).getAmount())
					.reduce(BigDecimal.ZERO,BigDecimal::add);
			tradesMap.put(e.getKey(), new BidderAllocation(value, e.getValue().getBundle(), e.getValue().getAcceptedBids()));
		}
		
		Set<PotentialCoalition> potentialCoalitions = null;
		if(originalOutome.getAllocation().getPotentialCoalitions() != null) {
			potentialCoalitions = originalOutome.getAllocation().getPotentialCoalitions().stream().map(p -> new PotentialCoalition(p.getBundle(), p.getBidder(), p.getValue().divide(this.scale,RoundingMode.HALF_UP))).collect(Collectors.toSet());
		}
		
		Allocation newAllocation = new Allocation(tradesMap, originalBids, originalOutome.getMetaInfo(), potentialCoalitions);
		return new Outcome(newPayment, newAllocation);
	}
}
