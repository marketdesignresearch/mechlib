package ch.uzh.ifi.ce.strategy;

import ch.uzh.ifi.ce.strategy.buckets.StrategyBucket;
import ch.uzh.ifi.ce.domain.Bid;
import ch.uzh.ifi.ce.domain.bidder.Value;

import java.util.Set;

public interface StrategySpace<B extends StrategyBucket,S extends Strategy> {

    S strategyOf(B bucket);

    Set<B> getBuckets();

    B bucketOf(Value value);

    default Bid applyStrategyTo(Value value) {
        return strategyOf(bucketOf(value)).apply(value);
    }

    /*static <S extends Strategy> StrategySpace< ValueTypeBucket,S> getStrategySpace(DomainGenerator domain, S initialStrategy) {
        List<ValueTypeBucket> buckets = domain.getValueTypes().stream().map(ValueTypeBucket::new).collect(Collectors.toList());
        return new BucketStrategySpace<>(buckets, initialStrategy);
    }*/
}