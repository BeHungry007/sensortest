package com.zshield.stream.violation.metric;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.zshield.util.TimeUtil;
import org.apache.kafka.streams.state.KeyValueStore;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MetricBin {
    @SerializedName("METRIC")
    private Metric metric;
    @SerializedName("TIME")
    private OffsetDateTime time;
    @SerializedName("COMPUTER")
    private String computer;
    @SerializedName("METRIC_VALUE")
    private Map<String ,Integer> metricValue = new HashMap();
    @SerializedName("USERNAME")
    private String userName;

    public String getDocId() {
        return "metric-" + metric.getMetricId() + "-" + time.format(TimeUtil.DATE_HOUR_FORMATTER);
    }

    public static Set<MetricBin> create(JsonObject obj, Set<Metric> metrics) {
        Set<MetricBin> metricBins = new HashSet<>();
        metrics.forEach(metric -> metricBins.add(create(obj, metric)));
        return metricBins;
    }

    public static MetricBin create(JsonObject obj, Metric metric) {
        if (!obj.has("USERNAME"))  {
            return new MetricBin(metric, obj.get("TIME").getAsString(), obj.get("COMPUTER").getAsString(), "");
        }
        return new MetricBin(metric, obj.get("TIME").getAsString(), obj.get("COMPUTER").getAsString(), obj.get("USERNAME").getAsString());
    }

    public void update(KeyValueStore<String,String> kv, JsonObject obj) {
        String docId = getDocId();
        String oldValue = kv.get(docId);
        Gson gson = new Gson();
        for (MetricUpdate metricUpdate : metric.getMetricUpdate()) {
            String field = metricUpdate.getField();
            if (oldValue == null) {
                if (metricUpdate.isDefaultMetricUpdate()) {
                    metricValue.put(field, 1);
                } else {
                    metricValue.put(field, obj.get(field).getAsInt());
                }
            } else {
               Map<String, Integer> metricMap = gson.fromJson(oldValue, new TypeToken<Map<String, Integer>>(){}.getType());
               if (metricUpdate.isDefaultMetricUpdate()) {
                   metricValue.put(field, 1 + metricMap.get(field));
               } else {
                   metricValue.put(field, obj.get(field).getAsInt() + metricMap.get(field));
               }
            }

            kv.put(docId, gson.toJson(metricValue));
        }
    }

    public MetricBin(Metric metric, String time, String computer, String userName) {
        this.metric = metric;
        this.time = TimeUtil.parseToEastEightZoneTime(time);
        this.computer = computer;
        this.userName = userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Metric getMetric() {
        return metric;
    }

    public OffsetDateTime getTime() {
        return time;
    }

    public String getComputer() {
        return computer;
    }

    public Map<String, Integer> getMetricValue() {
        return metricValue;
    }

    public String getUserName() {
        return userName;
    }


    public String metricBinInfo() {
        return null;
    }
}
