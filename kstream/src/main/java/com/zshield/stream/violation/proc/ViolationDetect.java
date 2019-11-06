package com.zshield.stream.violation.proc;

import com.google.gson.JsonParser;
import com.zshield.httpServer.common.ViolationShared;
import com.zshield.stream.violation.detection.Detection;
import com.zshield.stream.violation.detection.DetectionResult;
import com.zshield.stream.violation.metric.MetricBin;
import com.zshield.stream.violation.metric.MetricFactory;
import org.apache.kafka.streams.processor.AbstractProcessor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class ViolationDetect extends AbstractProcessor<String, MetricBin> {
    private ProcessorContext context;
    private KeyValueStore<String,String> kv;
    private final JsonParser jp = new JsonParser();
    private final Map<String, String> newSensorTime = new HashMap<>();
    private MetricFactory metricFactory;
    private final static Logger logger = LoggerFactory.getLogger(ViolationDetect.class);

    @Override
    public void init(ProcessorContext context){
        metricFactory = new MetricFactory();

    }

    /**
     * 根据违规检测对象获取需要检测的集合。
     * @param key
     * @param metricBin
     */
    @Override
    public void process(String key, MetricBin metricBin) {
        Set<Detection> detections = ViolationShared.getInstance().getDetections(metricBin.getMetric().getMetricId());
        if (detections != null && detections.size() > 0) {
            detections.forEach(detection -> {
                //获取违规检测结果
                DetectionResult result = detection.detect(metricBin, kv);
                if (result != null) {
                    ViolationShared.getInstance().sendCaveat(result);
                    logger.info("[DetectionResult: {}-{}-{}-{}-{}]", result.getRuleId(), result.getSensorId());
                }
            });
        }
    }
}
