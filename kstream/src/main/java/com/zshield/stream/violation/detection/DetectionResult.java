package com.zshield.stream.violation.detection;


import com.zshield.httpServer.domain.RuleType;
import com.zshield.stream.violation.metric.MetricBin;

import java.util.Objects;
import java.util.PrimitiveIterator;

public class DetectionResult {
    private String ruleId;
    private String sensorId;
    private String baseViolationId;
    private RuleType ruleType;
    private String userName;
    private String message;
    private Long timestamp;

    public static DetectionResult build(AbstractDetection detection, MetricBin metricBin, String baseViolationId, String violationInfo) {
        String userName = metricBin.getUserName();
        String message = violationInfo;
        RuleType ruleType = detection.getRuleType();
        String sensorId = metricBin.getMetric().getSensorId();
        String ruleId = detection.getRuleId();
        return new DetectionResult(ruleId, sensorId, baseViolationId, ruleType, userName, message);
    }

    public String getDetectionResultId() {
        return ruleId + "-" + sensorId + "-" + ruleType + "-" + userName + "-" + baseViolationId;
    }

    @Override
    public String toString() {
        return "DetectionResult{" +
                "ruleId='" + ruleId + '\'' +
                ", sensorId='" + sensorId + '\'' +
                ", baseViolationId='" + baseViolationId + '\'' +
                ", ruleType=" + ruleType +
                ", userName='" + userName + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DetectionResult that = (DetectionResult) o;
        return Objects.equals(ruleId, that.ruleId) &&
                Objects.equals(sensorId, that.sensorId) &&
                Objects.equals(baseViolationId, that.baseViolationId) &&
                Objects.equals(ruleType, that.ruleType) &&
                Objects.equals(userName, that.userName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(ruleId, sensorId, baseViolationId, ruleType, userName);
    }

    public DetectionResult(String ruleId, String sensorId, String baseViolationId, RuleType ruleType, String userName, String message) {
        this.ruleId = ruleId;
        this.sensorId = sensorId;
        this.baseViolationId = baseViolationId;
        this.ruleType = ruleType;
        this.userName = userName;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public String getBaseViolationId() {
        return baseViolationId;
    }

    public RuleType getRuleType() {
        return ruleType;
    }

    public void setRuleType(RuleType ruleType) {
        this.ruleType = ruleType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTimestamp() {
        return timestamp;
    }

}
