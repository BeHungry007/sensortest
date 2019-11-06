package com.zshield.stream.violation.metric;

import java.util.Set;

public interface Metric {
    //形式为：sensorId + "-" + format + "-" + accessFormat + "-" + fileExt.split(",")[0]
    public  String getMetricId();

    public Set<MetricUpdate> getMetricUpdate();

    public String getSensorId();

    public String getMetricInfo();
}
