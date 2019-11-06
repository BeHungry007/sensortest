package com.zshield.stream.violation.detection;

import com.zshield.httpServer.domain.RuleType;
import com.zshield.stream.violation.detection.MetricsDetection.MetricsDetection;
import com.zshield.stream.violation.detection.violationTime.TimeRange;
import com.zshield.stream.violation.metric.MetricBin;
import com.zshield.stream.violation.metric.MetricUpdate;

import java.util.Set;

/**
 * 基于TimeRange的违规检测。
 * 1.MetricUpdate，如：ACCESS_TIMES=5次和OPERATE_SIZE=80M
 * 2.ruleId,违规定义的id
 * 3.ruleName,违规定义的id
 * 4.detectionValue,违规定义的阈值
 * 5.timeRange，违规定义的TimeRange
 */
public class TimeRangeViolationDetection extends TimeBasedDetection {
    private TimeRange timeRange;
    public TimeRangeViolationDetection(MetricUpdate metricUpdate, String ruleId, String ruleName, int detectionValue, RuleType ruleType, MetricsDetection metricsDetection) {
        super(metricUpdate, ruleId, ruleName, detectionValue, ruleType, metricsDetection);
    }

    @Override
    public Set<String> getTimeHours(MetricBin metricBin) {
        return timeRange.getTimeHours(metricBin.getTime());
    }

    @Override
    public boolean detectionCondition(MetricBin metricBin) {
        return false;
    }
}
