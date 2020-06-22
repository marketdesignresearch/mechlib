package org.marketdesignresearch.mechlib.core.bundlesampling;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Good;

import java.util.List;

/**
 * This interface is implemented by bundle sampling methods that are based on a list of goods.
 */
public interface BundleSampling {
    /**
     * Get a single good based on a list of goods.
     *
     * @param goods a list of {@link Good}s
     * @return a sampled {@link Bundle}
     */
    Bundle getSingleBundle(List<? extends Good> goods);
}
