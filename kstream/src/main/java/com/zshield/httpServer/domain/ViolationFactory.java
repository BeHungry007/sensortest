package com.zshield.httpServer.domain;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import static com.zshield.httpServer.domain.RuleType.FileAccessMonitor;
import static com.zshield.httpServer.domain.RuleType.HardwareLoadMonitor;
import static com.zshield.httpServer.domain.RuleType.PeripheralUseMonitor;
import static com.zshield.httpServer.domain.RuleType.ProgramRunningMonitor;
import static com.zshield.httpServer.domain.RuleType.SensorInvaidPortAccessMonitor;
import static com.zshield.httpServer.domain.RuleType.SystemConfigMonitor;



public class ViolationFactory {

    public static Gson gson = new Gson();
    public static JsonParser jsonParser = new JsonParser();
    //Constructs a new type literal. Derives represented class from type parameter.
    //构造一个新类型文字。从类型参数派生表示的类。
    public static Type FileAccessJsonType = new TypeToken<Violation<FileAccessMonitor>>(){}.getType();
    public static Type HardwareLoadJsonType = new TypeToken<Violation<HardwareLoadMonitor>>(){}.getType();
    public static Type PeripheralUseJsonType = new TypeToken<Violation<PeripheralUseMonitor>>(){}.getType();
    public static Type ProgramRunningJsonType = new TypeToken<Violation<ProgramRunningMonitor>>(){}.getType();
    public static Type SensorInvaidPortAccessJsonType = new TypeToken<Violation<SensorInvaidPortAccessMonitor>>(){}.getType();
    public static Type SystemConfigJsonType = new TypeToken<Violation<SystemConfigMonitor>>(){}.getType();


    public static Violation parsingData(JsonObject jsonObject) {
        try {
            Violation violation = null;
            //Convenience method to check if a member with the specified name is present in this object.
            //方法检查具有指定名称的成员是否存在于此对象中。
            if (jsonObject.has("rule_type")) {
                String rule_type = jsonObject.get("rule_type").getAsString();
                if (rule_type.equals(FileAccessMonitor.value())){
                    //此方法将从指定解析树读取的Json反序列化为指定类型的对象。如果指定的对象是泛型类型，则此方法非常有用。
                    // 对于非泛型对象，可以使用{@link #fromJson(JsonElement, Class)}。
                    violation = gson.fromJson(jsonObject, FileAccessJsonType);
                    violation.setRule_type(RuleType.FileAccessMonitor);
                } else if (rule_type.equals(HardwareLoadMonitor.value())){
                    violation = gson.fromJson(jsonObject, HardwareLoadJsonType);
                    violation.setRule_type(RuleType.HardwareLoadMonitor);
                } else if (rule_type.equals(PeripheralUseMonitor.value())){
                    violation = gson.fromJson(jsonObject, PeripheralUseJsonType);
                    violation.setRule_type(RuleType.PeripheralUseMonitor);
                } else if (rule_type.equals(ProgramRunningMonitor.value())){
                    violation = gson.fromJson(jsonObject, ProgramRunningJsonType);
                    violation.setRule_type(RuleType.ProgramRunningMonitor);
                } else if (rule_type.equals(SensorInvaidPortAccessMonitor.value())){
                    violation = gson.fromJson(jsonObject, SensorInvaidPortAccessJsonType);
                    violation.setRule_type(RuleType.SensorInvaidPortAccessMonitor);
                } else if (rule_type.equals(SystemConfigMonitor.value())){
                    violation = gson.fromJson(jsonObject, SystemConfigJsonType);
                    violation.setRule_type(RuleType.SystemConfigMonitor);
                };
                if(violation != null){
                    violation.setTimestamp(System.currentTimeMillis());
                }
            }
            return violation;
        } catch (Exception e) {
            System.out.println("解析错误");
        }
        return null;
    }
}
