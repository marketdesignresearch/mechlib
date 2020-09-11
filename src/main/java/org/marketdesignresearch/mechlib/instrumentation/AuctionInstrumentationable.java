package org.marketdesignresearch.mechlib.instrumentation;

public interface AuctionInstrumentationable {
	default AuctionInstrumentation getAuctionInstrumentation() {
		return new AuctionInstrumentation();
	}

	void setAuctionInstrumentation(AuctionInstrumentation auctionInstrumentation);
}
