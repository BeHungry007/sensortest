package com.zshield.stream.violation.metric;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MetricUpdate {
    public final static MetricUpdate LOG_COUNT_UPDATE = new MetricUpdate("ACCESS_TIMES");
    public static Map<String, MetricUpdate> metricUpdates = new HashMap<>();
    static {
        metricUpdates.put("ACCESS_TIMES", LOG_COUNT_UPDATE);
    }
    private String field;

    public MetricUpdate(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }

    public boolean isDefaultMetricUpdate() {
        return this == LOG_COUNT_UPDATE;
    }

    public static MetricUpdate getMetricUpdate(String field) {
        MetricUpdate metricUpdate = metricUpdates.get(field);
        if (metricUpdate != null) {
            return metricUpdate;
        }
        metricUpdate = new MetricUpdate(field);
        metricUpdates.put(field, metricUpdate);
        return metricUpdate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof MetricUpdate)) {
            return false;
        } else {
            MetricUpdate other = (MetricUpdate) o;
            return other.field == this.field;
        }
    }

    @Override
    public String toString() {
        return "MetricUpdate{" +
                "field='" + field + '\'' +
                '}';
    }

    @Override
    public int hashCode() {

        return Objects.hash(field);
    }
}
