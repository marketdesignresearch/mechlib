package org.marketdesignresearch.mechlib.core.bidder;

import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

import org.marketdesignresearch.mechlib.core.BundleEntry;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.BundleValue;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.ORValueFunction;
import org.springframework.data.annotation.PersistenceConstructor;

import com.google.common.base.Preconditions;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class AdditiveValueBidder extends ORBidder {

	private static final long serialVersionUID = -4775422936024336326L;

	public AdditiveValueBidder(String name, ORValueFunction value) {
		this(UUID.randomUUID(), name, value);
	}

	@PersistenceConstructor
	private AdditiveValueBidder(UUID id, String name, ORValueFunction value) {
		super(id, name, value);
		for (BundleValue bundleValue : value.getBundleValues()) {
			List<BundleEntry> bundleEntries = bundleValue.getBundle().getBundleEntries();
			Preconditions.checkArgument(bundleEntries.size() == 1);
			Preconditions.checkArgument(bundleEntries.get(0).getAmount() == 1);
		}
		Preconditions.checkArgument(value.getBundleValues().stream().map(be -> be.getBundle().getSingleGood())
				.distinct().count() == value.getBundleValues().size());
		StringBuilder sb = new StringBuilder(
				"Bidder with an additive value function with the following values per item (rounded):");
		for (BundleValue bundleValue : getValueFunction().getBundleValues()) {
			sb.append("\n\t- ").append(bundleValue.getBundle()).append(": ")
					.append(bundleValue.getAmount().setScale(2, RoundingMode.HALF_UP));
		}
		setDescription(sb.toString());
		setShortDescription("Additive Value Bidder: " + getName());
	}

}
