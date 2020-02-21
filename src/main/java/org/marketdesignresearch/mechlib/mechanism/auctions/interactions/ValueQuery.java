package org.marketdesignresearch.mechlib.mechanism.auctions.interactions;

import java.util.Set;

import org.marketdesignresearch.mechlib.core.Bundle;

public interface ValueQuery extends Interaction {
	Set<Bundle> getQueriedBundles();
}
