package com.zshield.httpServer.domain;

public enum  RuleType {

    FileAccessMonitor("fileAccessMonitor", 8, 403),
    HardwareLoadMonitor("hardwareLoadMonitor", 8, 415),
    PeripheralUseMonitor("peripheralUseMonitor", 8, 405),
    ProgramRunningMonitor("programRunningMonitor", 8, 402),
    SensorInvaidPortAccessMonitor("sensorInvaidPortAccessMonitor", 8, 417),
    SystemConfigMonitor("systemConfigMonitor", 8, 407);

    private final String ruleType;
    private final int src_id;
    private final int src_alarm_name_id;

    RuleType(String ruleType, int src_id, int src_alarm_name_id) {
        this.ruleType = ruleType;
        this.src_id = src_id;
        this.src_alarm_name_id = src_alarm_name_id;
    }

    public  String value(){
        return this.ruleType;
    }

    public int getSrc_id() {
        return src_id;
    }

    private int getSrc_alarm_name_id(){
        return src_alarm_name_id;
    }
}
