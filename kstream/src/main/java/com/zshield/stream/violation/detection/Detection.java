package com.zshield.stream.violation.detection;

import com.zshield.stream.violation.metric.MetricBin;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.streams.state.KeyValueStore;

public interface Detection {
    public DetectionResult detect(MetricBin metricBin, KeyValueStore<String, String> kv);
    public String getEarliestMetricHourTime(String newTime);
}
