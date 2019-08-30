package org.marketdesignresearch.mechlib.instrumentation;

public interface MipInstrumentationable {
    default MipInstrumentation getMipInstrumentation() {
        return new MipInstrumentation();
    }
    void setMipInstrumentation(MipInstrumentation mipInstrumentation);
}
