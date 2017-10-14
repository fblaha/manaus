package cz.fb.manaus.core.metrics;

import com.google.common.base.MoreObjects;

public class MetricRecord<T> {
    private final String name;
    private final T value;

    public MetricRecord(String name, T value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public T getValue() {
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
