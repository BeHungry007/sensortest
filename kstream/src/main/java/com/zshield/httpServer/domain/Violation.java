package com.zshield.httpServer.domain;

import com.google.gson.annotations.SerializedName;
import com.zshield.stream.violation.detection.Detection;
import com.zshield.stream.violation.metric.Metric;

import java.util.Objects;
import java.util.Set;

import static com.zshield.httpServer.domain.RuleType.ProgramRunningMonitor;

public class Violation<T> {
    @SerializedName("rule_id")
    private String rule_id;
    @SerializedName("description")
    private String description;
    @SerializedName("enable")
    private int enable;
    @SerializedName("control_strategy")
    private String control_strategy;
    @SerializedName("rule")
    private T rule;
    @SerializedName("sensor_groupids")
    private Set<Integer> sensor_groupids;
    private RuleType rule_type;
    private Long timestamp;

    public interface Conversion {
        default Integer getProgramGroup() {
            return null;
        }

        default Set<Metric> conversionMetricsByprogramid(Set<String> sensor_ids, String programid) {
            return null;
        }

        Set<Metric> conversionMetrics(Set<String> sensor_ids);
        Set<Metric> conversionMetricsBysensorid(String sensorId);
        Detection conversionDetections(String ruleId, String ruleName, RuleType ruleType);

    }

    public boolean isIssued(){
        if (enable == 0 || sensor_groupids.size() == 0 || control_strategy.equals("log")){
            return false;
        }
        if (rule instanceof ProgramRunningMonitor) {

        }
        return false;
    }
    @Override
    public String toString() {
        return "Violation{" +
                "rule_id='" + rule_id + '\'' +
                ", description='" + description + '\'' +
                ", enable=" + enable +
                ", control_strategy='" + control_strategy + '\'' +
                ", rule=" + rule +
                ", sensor_groupids=" + sensor_groupids +
                ", rule_type=" + rule_type +
                ", timestamp=" + timestamp +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Violation<?> violation = (Violation<?>) o;
        return Objects.equals(rule_id, violation.rule_id) &&
                Objects.equals(description, violation.description) &&
                Objects.equals(control_strategy, violation.control_strategy) &&
                Objects.equals(rule, violation.rule) &&
                Objects.equals(sensor_groupids, violation.sensor_groupids) &&
                rule_type == violation.rule_type;
    }

    @Override
    public int hashCode() {

        return Objects.hash(rule_id, description, control_strategy, rule, sensor_groupids, rule_type);
    }

    public String getRule_id() {
        return rule_id;
    }

    public void setRule_id(String rule_id) {
        this.rule_id = rule_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getEnable() {
        return enable;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }

    public T getRule() {
        return rule;
    }

    public void setRule(T rule) {
        this.rule = rule;
    }

    public Set<Integer> getSensor_groupids() {
        return sensor_groupids;
    }

    public void setSensor_groupids(Set<Integer> sensor_groupids) {
        this.sensor_groupids = sensor_groupids;
    }

    public RuleType getRule_type() {
        return rule_type;
    }

    public void setRule_type(RuleType rule_type) {
        this.rule_type = rule_type;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
