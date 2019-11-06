package com.zshield.stream.violation.metric;

import com.google.gson.annotations.SerializedName;
import com.zshield.annotation.Format;
import org.apache.kafka.common.protocol.types.Field;

import java.util.HashSet;
import java.util.Set;

@Format({"SENSOR_FILESYSTEM"})
public class FileAccessShareMetric  extends AbstractMetric {
    @SerializedName("format")
    private String format;
    @SerializedName("sensorId")
    private String sensorId;
    @SerializedName("accessFormat")
    private String accessFormat;
    @SerializedName("fileExt")
    private String fileExt;

    /**
     * 定义该Metric的更新类型。
     * @return
     */
    @Override
    public Set<MetricUpdate> getMetricUpdate() {
        Set<MetricUpdate> metricUpdates = new HashSet<>();
        metricUpdates.add(MetricUpdate.LOG_COUNT_UPDATE);
        metricUpdates.add(MetricUpdate.getMetricUpdate("OPERATE_SIZE"));
        return metricUpdates;
    }

    /**
     * 生成违规定义的报警日志
     * @return
     */
    @Override
    public String getMetricId() {
        return sensorId + "-" + format + "-" + accessFormat + "-" + fileExt.split(",")[0];
    }

    @Override
    public String getSensorId() {
        return sensorId;
    }

    @Override
    public String getMetricInfo() {
        return "通过=" + accessFormat + "=[|" + fileExt.split(",")[0] + "|] 类型文件";
    }

    public FileAccessShareMetric(String format, String sensorId, String accessFormat, String fileExt) {
        this.format = format;
        this.sensorId = sensorId;
        this.accessFormat = accessFormat;
        this.fileExt = fileExt;
    }

    @Override
    public String toString() {
        return "FileAccessShareMetric{" +
                "format='" + format + '\'' +
                ", sensorId='" + sensorId + '\'' +
                ", accessFormat='" + accessFormat + '\'' +
                ", fileExt='" + fileExt + '\'' +
                '}';
    }


}
