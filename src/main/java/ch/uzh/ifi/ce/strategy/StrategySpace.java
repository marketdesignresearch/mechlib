package ch.uzh.ifi.ce.strategy;

import ch.uzh.ifi.ce.strategy.buckets.StrategyBucket;
import ch.uzh.ifi.ce.strategy.buckets.ValueTypeBucket;
import ch.uzh.ifi.ce.domain.Bid;
import ch.uzh.ifi.ce.domain.Value;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface StrategySpace<B extends StrategyBucket,S extends Strategy> {

    S strategyOf(B bucket);

    Set<B> getBuckets();

    B bucketOf(Value value);

    default Bid applyStrategyTo(Value value) {
        return strategyOf(bucketOf(value)).apply(value);
    }

    ;

    /*static <S extends Strategy> StrategySpace< ValueTypeBucket,S> getStrategySpace(DomainGenerator domain, S initialStrategy) {
        List<ValueTypeBucket> buckets = domain.getValueTypes().stream().map(ValueTypeBucket::new).collect(Collectors.toList());
        return new BucketStrategySpace<>(buckets, initialStrategy);
    }*/
}