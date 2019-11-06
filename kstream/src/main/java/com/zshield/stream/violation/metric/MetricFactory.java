package com.zshield.stream.violation.metric;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zshield.annotation.Format;
import com.zshield.httpServer.common.ViolationGroupToRule;
import com.zshield.httpServer.domain.Violation;
import com.zshield.util.ScanningPacakageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class MetricFactory {
    private static final Logger logger = LoggerFactory.getLogger(MetricFactory.class);
    private final String meticPackage = "com.zshield.stream.violation.metric";
    private Map<String, Set<Class<?>>> formatToMetric = new HashMap<>();
    private final JsonParser jp = new JsonParser();

    public MetricFactory() {
        ScanningPacakageUtil scanningPacakageUtil = new ScanningPacakageUtil(AbstractMetric.class);
        scanningPacakageUtil.addClass(meticPackage);
        List<Class<?>> metricList = scanningPacakageUtil.getEleStrategyList();
        for (Class<?> clazz : metricList) {
            //获取类的注解
            Format annotation = clazz.getAnnotation(Format.class);
            if (annotation == null) {
                logger.warn("Class " + clazz + " do not use Format annotation.");
                continue;
            }
            Arrays.stream(annotation.value()).forEach(format -> {
                Set<Class<?>> clazzs = formatToMetric.get(format);
                if (clazzs == null) {
                    clazzs = new HashSet<>();
                    formatToMetric.put(format, clazzs);
                }
                clazzs.add(clazz);
            });
        }
    }

    public static Set<Metric> build(Violation violation) {
        ViolationGroupToRule group = ViolationGroupToRule.getInstance();
        Set<String> sensor_ids = new HashSet<>();
        Set<Integer> sensor_groupids = violation.getSensor_groupids();
        for (Integer sensor_groupid : sensor_groupids) {
            sensor_ids.addAll(group.getSensorGroupIdToRuleId(sensor_groupid));
        }
        return ((Violation.Conversion)violation.getRule()).conversionMetrics(sensor_ids);
    }

    public Set<Metric> build(String log) {
        Set<Metric> metrics = new HashSet<>();
        JsonObject obj = jp.parse(log).getAsJsonObject();
        //以log日志中的注解获取到注解value一致的class集合。
        Set<Class<?>> clazzs = formatToMetric.get(obj.get("FORMAT").getAsString());
        if (clazzs == null) {
            return null;
        }
        clazzs.forEach( clazz -> metrics.add((Metric) new Gson().fromJson(log, clazz)));
        return metrics;
    }


}
