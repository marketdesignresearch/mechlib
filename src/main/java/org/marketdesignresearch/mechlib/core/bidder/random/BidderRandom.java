package org.marketdesignresearch.mechlib.core.bidder.random;

import java.util.Random;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum BidderRandom {
	INSTANCE;
	
	private ThreadLocal<Random> threadLocal = new ThreadLocal<>();
	
	public Random getRandom() {
		if(threadLocal.get() == null) {
			log.warn("Random seed not set. Results will not be reproducable");
			threadLocal.set(new Random());
		}
		return threadLocal.get();
	}
	
	public void setRandom(Random random) {
		threadLocal.set(random);
	}
}
