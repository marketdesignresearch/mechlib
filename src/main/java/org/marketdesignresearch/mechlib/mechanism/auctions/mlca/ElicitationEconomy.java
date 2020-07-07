package org.marketdesignresearch.mechlib.mechanism.auctions.mlca;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;

import com.google.common.base.Preconditions;

import lombok.Getter;

public class ElicitationEconomy {

	@Getter
	private final List<UUID> bidders;

	private final String name;

	public ElicitationEconomy(Domain domain) {
		this.bidders = domain.getBidders().stream().map(b -> b.getId()).collect(Collectors.toList());
		this.name = "Main Economy";
	}

	public ElicitationEconomy(Domain domain, Bidder bidder) {
		Preconditions.checkArgument(domain.getBidders().contains(bidder));
		this.bidders = domain.getBidders().stream().filter(b -> !b.equals(bidder)).map(b -> b.getId())
				.collect(Collectors.toList());
		this.name = "Marginal w/ excluded Bidder " + bidder.getName();
	}

	@Override
	public String toString() {
		return name;
	}
}
