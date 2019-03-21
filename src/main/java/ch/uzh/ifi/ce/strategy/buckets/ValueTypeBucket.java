package ch.uzh.ifi.ce.strategy.buckets;

import ch.uzh.ifi.ce.domain.ValueType;

/**
 * Created by buenz on 28.01.16.
 */
public class ValueTypeBucket implements StrategyBucket, Comparable<ValueTypeBucket> {
    private final ValueType type;

    public ValueTypeBucket(ValueType type) {
        this.type = type;
    }

    public ValueType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Bucket[type=" + type + "]";
    }

    @Override
    public int hashCode() {
        return type.ordinal();
    }

    @Override
    public boolean equals(Object other) {
        ValueTypeBucket otherBucket = (ValueTypeBucket) other;
        return getType() == otherBucket.getType();
    }

    @Override
    public int compareTo(ValueTypeBucket other) {
        return type.compareTo(other.type);
    }


}
