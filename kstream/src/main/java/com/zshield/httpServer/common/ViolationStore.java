package com.zshield.httpServer.common;

import com.zshield.httpServer.domain.Violation;
import com.zshield.stream.violation.detection.MetricsDetection.MetricsDetection;

import java.util.HashMap;
import java.util.Map;

public class ViolationStore {

    private Map<String, MetricDetection> ruleIdToMetricAndDetection = new HashMap<>();
    private Map<String, Violation> violations = new HashMap<>();
    private ViolationStore(){}

    public boolean containsRuleId(String rule_id) {
        return ruleIdToMetricAndDetection.containsKey(rule_id) && violations
                .containsKey(rule_id);
    }

    public static class SingletonPatternHolder {
        public static final ViolationStore violationStore = new ViolationStore();
    }

    public static ViolationStore getInstance() {
        return SingletonPatternHolder.violationStore;
    }

    public MetricDetection getMetricDetecion(String rule_id) {
        return ruleIdToMetricAndDetection.get(rule_id);
    }
}
