package com.aa.util;


import org.junit.Test;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.chrono.ChronoLocalDateTime;

public class TimeTest {
    @Test
    public void test01(){
        String logTime = "2007-12-03T10:15:30+01:00";
        OffsetDateTime parse = OffsetDateTime.parse(logTime);
        LocalDateTime nowAddOneDay = LocalDateTime.now().plusDays(1);
        System.out.println(nowAddOneDay);
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(logTime).withOffsetSameInstant(ZoneOffset.of("+08:00"));
        System.out.println(offsetDateTime);
        System.out.println("----------------");
        OffsetDateTime offsetDateTime2 = offsetDateTime.withOffsetSameInstant(ZoneOffset.of("+08:00"));
        System.out.println(offsetDateTime2);
        OffsetDateTime offsetDateTime1 = offsetDateTime.plusDays(-1);
        System.out.println(offsetDateTime1);
        System.out.println(offsetDateTime.compareTo(offsetDateTime1));
        System.out.println(ChronoLocalDateTime.from(offsetDateTime));
    }

    @Test
    public void test02() throws Exception{
        String logTime = "2007-12-03T10:15:30+01:00";
        String format = OffsetDateTime.parse(logTime).format(TimeUtil.DATE_FORMATTER);
        System.out.println(format);
        OffsetDateTime offsetDateTime1 = TimeUtil.parseToEastEightZoneTime(format);
        System.out.println(offsetDateTime1);
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(logTime).withOffsetSameInstant(ZoneOffset.of("+08:00"));
        System.out.println(offsetDateTime);
    }
}
