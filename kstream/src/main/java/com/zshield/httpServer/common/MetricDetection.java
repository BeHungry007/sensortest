package com.zshield.httpServer.common;

import com.zshield.stream.violation.detection.Detection;
import com.zshield.stream.violation.metric.Metric;

import java.util.Set;

public class MetricDetection {

    private Set<Metric> metrics;
    private Detection detection;

    public MetricDetection(Set<Metric> metrics, Detection detection) {
        this.metrics = metrics;
        this.detection = detection;
    }

    public Set<Metric> getMetrics() {
        return metrics;
    }

    public void setMetrics(Set<Metric> metrics) {
        this.metrics = metrics;
    }

    public Detection getDetection() {
        return detection;
    }

    public void setDetection(Detection detection) {
        this.detection = detection;
    }

    @Override
    public String toString() {
        return "MetricDetection{" +
                "metrics=" + metrics +
                ", detection=" + detection +
                '}';
    }
}
