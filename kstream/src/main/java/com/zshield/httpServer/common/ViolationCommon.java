package com.zshield.httpServer.common;

import com.zshield.httpServer.domain.Violation;
import com.zshield.stream.violation.detection.Detection;
import com.zshield.stream.violation.detection.DetectionFactory;
import com.zshield.stream.violation.metric.Metric;
import com.zshield.stream.violation.metric.MetricFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class ViolationCommon {
    
    private static final Logger logger = LoggerFactory.getLogger(ViolationCommon.class);

    private ViolationStore store = ViolationStore.getInstance();
    private ViolationShared shared = ViolationShared.getInstance();
    
    private ViolationCommon() {
        
    }

    public void detectionDependence(Violation violation) {
    }

    public boolean violationChange(Violation violation) {
        Set<Metric> metrics = MetricFactory.build(violation);
        Detection detection = DetectionFactory.build(violation);
        if (!store.containsRuleId(violation.getRule_id())) {
            addViolation(violation, metrics, detection);
        } else {
            updateViolation(violation, metrics, detection);
        }
        return true;
    }

    private void updateViolation(Violation violation, Set<Metric> metrics, Detection detection) {
    }

    private void addViolation(Violation violation, Set<Metric> metrics, Detection detection) {
        shared.addMetric(metrics, detection);

    }

    public static class SingletonPatternHolder {
        public static final ViolationCommon violationCommon = new ViolationCommon();
    }
    
    public static final ViolationCommon getInstance(){
        return SingletonPatternHolder.violationCommon;
    }
}
