package com.aa.util;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

public class TimeUtil {
    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd\'T\'HH:mm:ss+08:00");
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DATE_HOUR_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd\'T\'HH");
    public static final DateTimeFormatter DATE_FORMATTER_INDEX = DateTimeFormatter.ofPattern("yyyy.MM");
    public static final DateTimeFormatter DATE_FORMATTER_INDEX2 = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("1971-01-01\'T\'HH:mm:ss+08:00");
    public static final DateTimeFormatter HOUR_MINUTES_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public static String zoneParse(String time, String offsetId) {
        OffsetDateTime offDateTime = OffsetDateTime.parse(time);
        ZoneOffset zoneOffset = ZoneOffset.of(offsetId);
        OffsetDateTime offsetDateTime = offDateTime.withOffsetSameInstant(zoneOffset);
        return offsetDateTime.format(TimeUtil.DATETIME_FORMATTER);
    }

    public static OffsetDateTime parseToEastEightZoneTime(String time) {
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(time);
        return offsetDateTime.withOffsetSameInstant(ZoneOffset.of("+08:00"));
    }

    public static int getDaysOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public static String getTimeStamp() {
        return OffsetDateTime.now().withOffsetSameInstant(ZoneOffset.of("+08:00")).format(TimeUtil.DATETIME_FORMATTER);
    }

    public static Set<OffsetDateTime> getBasicDays(OffsetDateTime time, int size) {
        Set<OffsetDateTime> off = new LinkedHashSet<>();
        for (int i = 0; i < size; i++) {
            off.add(time);
            time = time.minusDays(1);
        }
        return off;
    }

    public static Set<String> getBasicHours(OffsetDateTime time, int size) {
        Set<String> off = new LinkedHashSet<>();
        for (int i = 0; i < size; i++) {
            off.add(time.format(TimeUtil.DATE_HOUR_FORMATTER));
            time = time.minusDays(1);
        }
        return off;
    }

    public static Set<OffsetDateTime> getAllDayByTheEndMonth(OffsetDateTime time) {
        //传入任意某一天的时间，返回所在月到当天的天数的集合
        return getBasicDays(time,time.getDayOfMonth());
    }

    public static Set<OffsetDateTime> getAllDayByMonth(OffsetDateTime time) {
        return getAllDayByTheEndMonth(time.plusDays(time.getMonth().maxLength() - time.getDayOfMonth()));
    }

    public static Set<OffsetDateTime> getAllDayByTheEndWeekend(OffsetDateTime time) {
        //传入任意某一天的时间，返回所在周到当天的天数的集合
        return getBasicDays(time,time.getDayOfWeek().getValue());
    }

    public static Set<OffsetDateTime> getAllDayByWeekend(OffsetDateTime time) {
        //传入任意某一天的时间，返回所在周的所有天数的集合
        return getAllDayByTheEndWeekend(time.plusDays(7 - time.getDayOfWeek().getValue()));
    }

    public static Set<String> getAllHourByTheEndDay(OffsetDateTime time) {
        //获取到当前时间的集合
        return getBasicHours(time,time.getHour() + 1);
    }

    public static Set<String> getAllHourByDay(OffsetDateTime time) {
        //传入任意某一天的市价你，返回该整天的集合
        return getAllHourByTheEndDay(time.plusHours(23 - time.getHour()));
    }

    public static Set<String> getAllHourByDaySet(Set<OffsetDateTime> timeSet) {
        Set<String> off = new LinkedHashSet<>();
        for(OffsetDateTime offset : timeSet) {
            off.addAll(getAllHourByDay(offset));
        }
        return off;
    }

    public static Set<String> getAllHourByEndWeek(OffsetDateTime time) {
        Set<String> timeHour = TimeUtil.getAllHourByTheEndDay(time);
        Set<OffsetDateTime> timeSet = TimeUtil.getAllDayByTheEndWeekend(time.minusDays(1));
        timeHour.addAll(TimeUtil.getAllHourByDaySet(timeSet));
        return timeHour;
    }

    public static Set<String> getAllHourByEndMonth(OffsetDateTime time) {
        Set<String> timeHour = TimeUtil.getBasicHours(time, time.getHour() + 1);
        Set<OffsetDateTime> timeSet = TimeUtil.getAllDayByTheEndMonth(time.minusDays(1));
        timeHour.addAll(TimeUtil.getAllHourByDaySet(timeSet));
        return timeHour;
    }

}
