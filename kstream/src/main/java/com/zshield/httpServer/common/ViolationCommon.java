package com.zshield.httpServer.common;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zshield.httpServer.ViolationIssuedThread;
import com.zshield.httpServer.config.ViolationServerConfig;
import com.zshield.httpServer.domain.Violation;
import com.zshield.httpServer.util.HttpClientUtil;
import com.zshield.stream.violation.detection.Detection;
import com.zshield.stream.violation.detection.DetectionFactory;
import com.zshield.stream.violation.metric.Metric;
import com.zshield.stream.violation.metric.MetricFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class ViolationCommon {
    
    private static final Logger logger = LoggerFactory.getLogger(ViolationCommon.class);
    private JsonParser json = new JsonParser();
    private Gson gson = new Gson();
    private ViolationStore store = ViolationStore.getInstance();
    private ViolationShared shared = ViolationShared.getInstance();
    private ViolationGroupToRule groupToRule = ViolationGroupToRule.getInstance();
    private ViolationCalendar violationCalendar = ViolationCalendar.getInstance();
    private ViolationIssuedThread violationIssuedThread = ViolationIssuedThread.getInstance();
    private ViolationCommon() {
        
    }

    public void detectionDependence(Violation violation) throws Throwable {
        Set<Integer> sensor_groupids = violation.getSensor_groupids();
        Violation.Conversion rule = (Violation.Conversion) violation.getRule();
        Integer programGroupId = rule.getProgramGroup();
        HttpClientUtil httpClient = HttpClientUtil.getInstance();

        Set<Integer> sensorGroupIds = new HashSet<>();
        for (Integer sensorGroupid : sensor_groupids) {
            if (!groupToRule.isRegisteredSensorGroupId(sensorGroupid)) {
                sensor_groupids.add(sensorGroupid);
            }
        }
        if (sensor_groupids.size() > 0) {
            logger.info("[ ruleid:{} dependence registered /registered/sensorid post]", violation.getRule_id());

            System.out.println(ViolationServerConfig.registeredSensorGroupIdsJson(sensor_groupids));
            String sensorIdsData = httpClient.post(ViolationServerConfig.BACKSTAGE_REGISTERED_SENSORID, ViolationServerConfig.registeredSensorGroupIdsJson(sensorGroupIds)).readEntity(String.class);
            JsonObject parse = json.parse(sensorIdsData).getAsJsonObject();
            if (parse.get("success").getAsBoolean() == true) {
                JsonArray result = parse.get("result").getAsJsonArray();
                logger.info("[ruleid:{} dependence");
            }
        }
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


    public boolean isRegisteredSensorGroupId(Integer sensorGroupId) {
        return groupToRule.isRegisteredSensorGroupId(sensorGroupId);
    }

    public boolean isRegisteredProgramGroupId(Integer programGroupId) {
        return groupToRule.isRegisteredProgramGroupId(programGroupId);
    }
}
