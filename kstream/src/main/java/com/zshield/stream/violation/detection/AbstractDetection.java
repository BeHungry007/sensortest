package com.zshield.stream.violation.detection;

import com.zshield.httpServer.domain.RuleType;
import com.zshield.stream.violation.detection.MetricsDetection.MetricsDetection;
import com.zshield.stream.violation.metric.Metric;
import com.zshield.stream.violation.metric.MetricBin;
import com.zshield.stream.violation.metric.MetricUpdate;
import org.apache.kafka.streams.state.KeyValueStore;

import java.util.Set;

public abstract class AbstractDetection implements Detection {

    protected String ruleId;
    protected String ruleName;
    protected RuleType ruleType;
    protected MetricsDetection metricsDetection;
    protected MetricUpdate metricUpdate;
    protected int detectionValue;

    public Set<Metric> getDetectionMetrics() {
        return null;
    }

    public String getBaseViolationId(String metricId) throws Exception {
        switch (metricsDetection) {
            case Parallel:
                return metricId;
            case Superimposed:
                return "";
            default:
                throw new Exception("Unsupported MetricsDetecion");
        }
    }

    public String getRuleId() {
        return ruleId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public RuleType getRuleType() {
        return ruleType;
    }

    public void setRuleType(RuleType ruleType) {
        this.ruleType = ruleType;
    }

    public AbstractDetection(String ruleId, String ruleName, RuleType ruleType, MetricsDetection metricsDetection, MetricUpdate metricUpdate, int detectionValue) {
        this.ruleId = ruleId;
        this.ruleName = ruleName;
        this.ruleType = ruleType;
        this.metricsDetection = metricsDetection;
        this.metricUpdate = metricUpdate;
        this.detectionValue = detectionValue;
    }




    @Override
    public String getEarliestMetricHourTime(String newTime) {
        return null;
    }
}
