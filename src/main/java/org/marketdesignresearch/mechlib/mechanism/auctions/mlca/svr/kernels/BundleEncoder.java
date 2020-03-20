package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr.kernels;

import org.marketdesignresearch.mechlib.core.Bundle;

public class BundleEncoder {
	private BundleEncoder() {} ;
	
	public static int getStandardDotProdWith(Bundle bundle1, Bundle bundle2) {
		return bundle1.getBundleEntries().stream().map(e -> e.getAmount() * bundle2.countGood(e.getGood())).reduce(Integer::sum).orElse(0);
	}
	
	public static int getIntersectionSizeWith(Bundle bundle1, Bundle bundle2) {
		return bundle1.getBundleEntries().stream().map(e -> Math.min(e.getAmount(),bundle2.countGood(e.getGood()))).reduce(Integer::sum).orElse(0);
	}
	
	public static int getUnionSizeWith(Bundle bundle1, Bundle bundle2) {
		return bundle1.getTotalAmount() + bundle2.getTotalAmount() - BundleEncoder.getIntersectionSizeWith(bundle1, bundle2);
	}
}
