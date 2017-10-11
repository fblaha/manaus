package cz.fb.manaus.core.metrics;

import com.google.common.base.MoreObjects;

public class MetricRecord {
    private final String name;
    private final double value;

    public MetricRecord(String name, double value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .add("value", value)
                .toString();
    }
}
