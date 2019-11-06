package com.zshield.httpServer.common;

import com.sun.xml.bind.v2.runtime.unmarshaller.XsiNilLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ViolationGroupToRule {

    private static final Logger logger = LoggerFactory.getLogger(ViolationGroupToRule.class);
    private Map<Integer, Set<String>> sensorGroupIdToRuleId = new HashMap<>();
    private Map<Integer, Set<String>> programGroupIdToRuleId = new HashMap<>();
    private Map<Integer, Set<String>> programGroupIds = new HashMap<>();
    private Map<Integer, Set<String>> sensorGroupIds = new HashMap<>();

    private ViolationGroupToRule() {}

    public Set<String> getSensorGroupIdToRuleId(Integer groupId) {
        return sensorGroupIdToRuleId.get(groupId);
    }

    public Set<String> getProgramGroupIdToRuleId(Integer groupId) {
        return programGroupIdToRuleId.get(groupId);
    }

    public void addSensorGroupId(Integer groupId, Set<String> sensorIds) {
        sensorGroupIds.put(groupId, sensorIds);
    }

    public void addProgramGroupId(Integer groupId, Set<String> programId) {
        programGroupIds.put(groupId, programId);
    }

    public static class SingtonPatternHolder {
        public static final ViolationGroupToRule violationGroupToRule = new ViolationGroupToRule();
    }

    public static ViolationGroupToRule getInstance() {
        return SingtonPatternHolder.violationGroupToRule;
    }

}
