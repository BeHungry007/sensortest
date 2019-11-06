package com.zshield.stream.violation.detection.violationTime;

import com.zshield.util.TimeUtil;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 违规定义中传入的时间日期范围
 * 1.Set<String>集合传入违规定义的时间日期，格式如：2019-05-07
 * 2.Map<String, String>中存放着起始时间和结束时间的键值对，格式如：08：30-10：00；
 * 3.主要用于判断MetricBin中存放的日期时间是否再设定的时间范围；
 * 4.判断的依据：1.如果Set<String>中的日期为空，则只需MetricBin中的日期在Map<String, String>任意的一个键值对
 *                    范围内，则认为在违规定义的时间范围内；
 *               2.如果Set<String>中的日期不为空，则MetricBin中的日期既要满足Set<String>设定的时间，又要满足
 *                    Map<String, String>设定设置的时间对。
 */
public class TimeRange {
    private Set<String> days;
    private Map<String, String> startAndEndTime;
    private Set<Integer> hours;

    public TimeRange(Set<String> days, Map<String, String> startAndEndTime) {
        this.days = days;
        this.startAndEndTime = startAndEndTime;
        hours = new HashSet<>();
        startAndEndTime.forEach((key, value) -> {
            LocalTime startTime = LocalTime.of(Integer.valueOf(key.split(":")[0]), 0);
            LocalTime endTime = LocalTime.of(Integer.valueOf(key.split(":")[0]), Integer.valueOf(value.split(":")[1]));
            while (startTime.compareTo(endTime) < 0) {
                hours.add(startTime.getHour());
                startTime = startTime.plusHours(1);
                if (startTime.getHour() == 0)
                    break;
            }

        });
    }

    /**
     * 判断传入的时期是否在违规定义的时间范围内。
     * @param time 传入的MetricBin中的日期TIME
     * @return
     */
    public boolean containTime(OffsetDateTime time) {
        String dayTime = time.format(TimeUtil.DATE_FORMATTER);
        String hourMinutes = time.format(TimeUtil.HOUR_MINUTES_FORMATTER);

        if (days != null) {
            if (days.contains(dayTime) && isInHourMinuteRange(hourMinutes, startAndEndTime)) {
                return true;
            }
            return false;
        } else {
            return isInHourMinuteRange(hourMinutes, startAndEndTime);
        }
    }

    /**
     * 判断传入的时间（格式：08：00）是否在任意一个Map<String, String>设定的键值对内
     *
     * @param hourMinutes  传入的时间，如08:00
     * @param startAndEndTimes
     * @return
     */
    private boolean isInHourMinuteRange(String hourMinutes, Map<String, String> startAndEndTimes) {
        for (Map.Entry<String, String> entry : startAndEndTimes.entrySet()) {
            String startTime = entry.getKey();
            String endTime = entry.getValue();
            if (hourMinutes.compareTo(startTime) > 0 && hourMinutes.compareTo(endTime) < 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据传入的时间日期，获取满足日期窗口时间集合，集合中的时间格式为：2019-05-07T11
     * @param time 传入的MetricBin中的TIME
     * @return 满足时间窗口时间集合。
     */
    public Set<String> getTimeHours(OffsetDateTime time) {
        Set<String> timeHours = new HashSet<>();
        String dayTime = time.format(TimeUtil.DATE_FORMATTER);
        //the hour-of-day, from 0 to 23
        Integer hourTime = time.getHour();
        hours.forEach((hour) -> {
            if (hour <= hourTime)
                timeHours.add(dayTime + "T" +hour);
        });
        return timeHours;
    }

    @Override
    public String toString() {
        return "TimeRange{" +
                "days=" + days +
                ", startAndEndTime=" + startAndEndTime +
                '}';
    }
}
