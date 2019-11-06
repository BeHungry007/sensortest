package com.zshield.stream.violation.detection;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zshield.httpServer.domain.RuleType;
import com.zshield.stream.violation.detection.MetricsDetection.MetricsDetection;
import com.zshield.stream.violation.metric.Metric;
import com.zshield.stream.violation.metric.MetricBin;
import com.zshield.stream.violation.metric.MetricUpdate;
import org.apache.kafka.streams.state.KeyValueStore;

import java.util.Map;
import java.util.Set;

public abstract class TimeBasedDetection extends AbstractDetection {
    public TimeBasedDetection(MetricUpdate metricUpdate, String ruleId, String ruleName, int detectionValue, RuleType ruleType, MetricsDetection metricsDetection) {
        super(ruleId, ruleName, ruleType, metricsDetection, metricUpdate, detectionValue);
    }

    public abstract Set<String> getTimeHours(MetricBin metricBin);

    public abstract boolean detectionCondition(MetricBin metricBin);

    public String getViolationKey(Metric metric, String hourTime) {
        String violation_key = "metric-" + metric.getMetricId() + "-" + hourTime;
        return violation_key;
    }

    protected int detectionValue(MetricBin metricBin, KeyValueStore<String,String> kv, Set<String> hourTimes) throws Exception{
        int metricValue = 0;
        String key;
        String value;
        Gson gson = new Gson();
        String sensorId = metricBin.getMetric().getSensorId();
        for (String hourTime : hourTimes) {
            switch (metricsDetection) {
                case Parallel:
                    key = getViolationKey(metricBin.getMetric(), hourTime);
                    value = kv.get(key);
                    if (value != null) {
                        Map<String, Integer> metricMap = gson.fromJson(value, new TypeToken<Map<String, Integer>>() {
                        }.getType());
                        metricValue += metricMap.get(metricUpdate.getField());
                    }
                    break;
                case Superimposed:
                    for (Metric metric : getDetectionMetrics()) {
                        if (sensorId.equals(metric.getSensorId())) {
                            key = getViolationKey(metric, hourTime);
                            value = kv.get(key);
                            if (value != null) {
                                Map<String, Integer> metricMap = gson.fromJson(value, new TypeToken<Map<String, Integer>>() {}.getType());
                                metricValue += metricMap.get(metricUpdate.getField());
                            }
                        }
                    }
                    break;
                default:
                    throw new Exception("Unsupported MetricsDetection");
            }
        }
        return metricValue;
    }

    /**
     * 基于时间的违规检测方案
     * 1.获取MetricBin中Metric的MetricId;
     * 2.基于MetricBin中的TIME,返回满足时间窗口的时间集合；
     * 3.根据第一步和第二部结果拼接成key;
     * 4.根据第三步中key，从keyvalueStore中查询出结果，进行增量计算；
     * 5.与detectionValue比较，大于detectionValue则认为违规；
     * 6.生成违规结果，并返回
     * @param  kv 需要判断的MetricBin 和 keyValueStore;
     * @return  返回检测结果。
     */
    public DetectionResult detect(MetricBin metricBin, KeyValueStore<String, String> kv) {
        //判断metricBin的时间是否再定义的时间内。如果不在返回null.
        if (!detectionCondition(metricBin)) {
            return null;
        }
        long metricValue = -1;
        //通过metricBin获取到metric中的时间，然后获取detection中早于这个时间的时间集合。
        Set<String> hourTimes = getTimeHours(metricBin);
        try {
            metricValue = detectionValue(metricBin, kv, hourTimes);
            if (metricValue <= detectionValue) {
                return null;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        String baseViolationId = null;
        try {
            baseViolationId = getBaseViolationId(metricBin.getMetric().getMetricId());
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
        return DetectionResult.build(this, metricBin, baseViolationId, generateDetectionInfo(metricBin, extraViolationInfo(metricValue)));
    }

    private String generateDetectionInfo(MetricBin metricBin, String extraInformation) {
        String baseInfo = metricBin.metricBinInfo() + "违反【" + ruleName + "】规则。";
        if (this.ruleType.equals("A")) {

        }
        return baseInfo;
    }

    private String extraViolationInfo(long curDetectValue) {
        return super.metricUpdate.isDefaultMetricUpdate() ? "操作次数为 " + curDetectValue + "次" :
                "操作文件大小为 " + parseDetectValue(curDetectValue);
    }

    private static String parseDetectValue(long curDetectValue) {
        if (curDetectValue / (1L << 40) > 0) {
            return String.format("%.2f", curDetectValue * 1.0 / (1L << 40)) + "TB";
        } else if (curDetectValue / (1L << 30) > 0) {
            return String.format("%.2f", curDetectValue * 1.0 / (1L << 30)) + "GB";
        } else if (curDetectValue / (1L << 20) > 0) {
            return String.format("%.2f", curDetectValue * 1.0 / (1L << 20)) + "MB";
        } else if (curDetectValue / (1L << 10) > 0) {
            return String.format("%.2f", curDetectValue * 1.0 / (1L << 10)) + "KB";
        } else {
            return curDetectValue + "B";
        }
    }

}
