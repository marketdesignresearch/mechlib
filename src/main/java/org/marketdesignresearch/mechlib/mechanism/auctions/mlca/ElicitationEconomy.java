package org.marketdesignresearch.mechlib.mechanism.auctions.mlca;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;

import com.google.common.base.Preconditions;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * An elicitation economy (i.e. main or marginal economy). 
 * Holds a list of all bidders' UUIDs belonging to this economy.
 * 
 * @author Manuel Beyeler
 */
@EqualsAndHashCode
public class ElicitationEconomy {

	@Getter
	private final List<UUID> bidders;

	private final String name;

	/**
	 * Creates the main economy for the given domain.
	 */
	public ElicitationEconomy(Domain domain) {
		this.bidders = domain.getBidders().stream().map(b -> b.getId()).collect(Collectors.toList());
		this.name = "Main Economy";
	}

	/**
	 * Creates a marginal economy for the given domain where the given bidder is excluded. 
	 */
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
