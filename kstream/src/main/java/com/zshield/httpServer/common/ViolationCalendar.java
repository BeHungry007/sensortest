package com.zshield.httpServer.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ViolationCalendar {
    private static final Logger logger = LoggerFactory.getLogger(ViolationCalendar.class);
    private static boolean isRegistered = false;
    private Set<String> calendarToRuleId = new HashSet<>();
    private Map<String, String> calendar;
    private String startTime;
    private String endTime;

    private ViolationCalendar(){}

    private static class SingletonPatternHolder {
        private static final ViolationCalendar calendar = new ViolationCalendar();
    }

    public static final ViolationCalendar getInstance() {
        return SingletonPatternHolder.calendar;
    }



}
