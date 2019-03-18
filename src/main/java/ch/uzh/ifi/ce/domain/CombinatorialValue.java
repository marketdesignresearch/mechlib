package ch.uzh.ifi.ce.domain;

import java.util.HashSet;
import java.util.Set;

public class CombinatorialValue implements Value {
    /**
     * 
     */
    private static final long serialVersionUID = -2661282710326907817L;
    private final Set<BundleValue> bundleValues;
    private final ValueType type;

    public CombinatorialValue(Set<BundleValue> bundleValues, ValueType type) {
        this.bundleValues = bundleValues;
        this.type = type;
    }

    public CombinatorialValue(ValueType type) {
        this(new HashSet<>(), type);
    }

    public boolean addBundleValue(BundleValue bundleValue) {
        return bundleValues.add(bundleValue);
    }

    /*
     * (non-Javadoc)
     * 
     * @see ch.uzh.ifi.ce.cca.domain.Value#getBundleValues()
     */
    @Override
    public Set<BundleValue> getBundleValues() {
        return bundleValues;
    }

    @Override
    public ValueType getValueType() {
        return type;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ch.uzh.ifi.ce.cca.domain.Value#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        Value otherValue = (Value) obj;
        return bundleValues.equals(otherValue.getBundleValues());
    }

    /*
     * (non-Javadoc)
     * 
     * @see ch.uzh.ifi.ce.cca.domain.Value#hashCode()
     */
    @Override
    public int hashCode() {
        return bundleValues.hashCode();
    }

    @Override
    public String toString() {
        return "Bid[bundleBids=" + bundleValues + "]";
    }

}
